"""Fetch pinned development APIs and prepare a compile-only intermediary classpath for Loom."""
import concurrent.futures
import hashlib
import io
import json
import os
from pathlib import Path
import tempfile
import time
import urllib.request
import zipfile

PROJECT = Path(__file__).resolve().parents[1]
DEV = PROJECT / '.backport-dev'
CACHE = DEV / 'downloads'


def digest(path):
    with path.open('rb') as stream:
        return hashlib.sha256(stream.read()).hexdigest()


def fetch(entry):
    path = CACHE / entry['file']
    if path.is_file() and digest(path) == entry['sha256']:
        return path
    request = urllib.request.Request(entry['url'], headers={'User-Agent': 'Orihime-1.18.2-development/1'})
    for attempt in range(3):
        try:
            with tempfile.NamedTemporaryFile(dir=CACHE, delete=False) as tmp:
                temp = Path(tmp.name)
                with urllib.request.urlopen(request, timeout=90) as response:
                    while chunk := response.read(1024 * 1024):
                        tmp.write(chunk)
            if digest(temp) != entry['sha256']:
                raise ValueError('SHA-256 mismatch for ' + entry['file'])
            os.replace(temp, path)
            return path
        except OSError:
            if attempt == 2:
                raise
            time.sleep(attempt + 1)
        finally:
            if 'temp' in locals():
                temp.unlink(missing_ok=True)


def prepare():
    lock = json.loads((PROJECT / 'gradle/backport-deps.json').read_text(encoding='utf-8'))
    CACHE.mkdir(parents=True, exist_ok=True)
    with concurrent.futures.ThreadPoolExecutor(max_workers=4) as pool:
        paths = list(pool.map(fetch, lock))
    seen, rules = set(), set()
    temp = DEV / 'compile-api.partial.jar'
    with zipfile.ZipFile(temp, 'w', zipfile.ZIP_DEFLATED) as out:
        def collect(source, packages, required):
            names = source.namelist()
            if 'fabric.mod.json' in names and not packages:
                mod = json.loads(source.read('fabric.mod.json'), strict=False)
                aw = mod.get('accessWidener')
                if required and aw:
                    lines = source.read(aw).decode('utf-8-sig').splitlines()
                    if lines[0].split()[-1] != 'intermediary':
                        raise ValueError('Unexpected access widener namespace: ' + aw)
                    for line in lines[1:]:
                        rule = line.split('#')[0].strip().removeprefix('transitive-')
                        if rule:
                            rules.add('transitive-' + rule)
                for child in mod.get('jars', []):
                    with zipfile.ZipFile(io.BytesIO(source.read(child['file']))) as nested:
                        collect(nested, packages, required)
            for name in names:
                if not name.endswith('.class') or name in seen or name.startswith(('META-INF/', 'net/minecraft/',
                        'net/fabricmc/', 'org/objectweb/', 'com/google/', 'org/apache/', 'org/slf4j/')) or name == 'module-info.class':
                    continue
                if packages and not name.startswith(packages):
                    continue
                out.writestr(name, source.read(name))
                seen.add(name)

        for entry, path in zip(lock, paths):
            with zipfile.ZipFile(path) as jar:
                collect(jar, tuple(entry.get('packages', [])), entry.get('required', False))
        out.writestr('backport-dev.accesswidener', 'accessWidener v2 intermediary\n' + '\n'.join(sorted(rules)) + '\n')
        out.writestr('fabric.mod.json', json.dumps({'schemaVersion': 1, 'id': 'orihime_dev_apis',
                     'version': '1', 'accessWidener': 'backport-dev.accesswidener'}))
    os.replace(temp, DEV / 'compile-api.jar')
    print(f'Prepared {len(paths)} pinned development dependencies in {DEV}')


if __name__ == '__main__':
    prepare()

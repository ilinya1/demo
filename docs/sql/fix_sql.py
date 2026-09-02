# -*- coding: utf-8 -*-
# Fix corrupted lines in init.sql (zero-width spaces + double commas + typo)
# This script is pure ASCII; Chinese strings are built via unicode escapes.
import pathlib

p = pathlib.Path('docs/sql/init.sql')
text = p.read_text(encoding='utf-8')

# 1. Remove invisible characters everywhere
for ch in ['\u200b', '\u200c', '\u200d', '\ufeff']:
    text = text.replace(ch, '')

lines = text.splitlines()

# Chinese strings via unicode escapes (avoid raw Chinese in this script)
HAO = '\u53f7\u697c'            # hao + lou (building suffix)
ZHANG = '\u5f20\u5bbf\u7ba1'    # zhang su guan
LI = '\u674e\u5bbf\u7ba1'       # li su guan
WANG = '\u738b\u5bbf\u7ba1'     # wang su guan
ZHAO = '\u8d75\u5bbf\u7ba1'     # zhao su guan

fixed = 0
for i, line in enumerate(lines):
    t = line.strip()
    if t.startswith("('1'"):
        lines[i] = "('1', '1" + HAO + "', 6, '" + ZHANG + "'),"
        fixed += 1
    elif t.startswith("('2'"):
        lines[i] = "('2', '2" + HAO + "', 6, '" + LI + "'),"
        fixed += 1
    elif t.startswith("('3'"):
        lines[i] = "('3', '3" + HAO + "', 5, '" + WANG + "'),"
        fixed += 1
    elif t.startswith("('4'"):
        lines[i] = "('4', '4" + HAO + "', 5, '" + ZHAO + "');"
        fixed += 1

text = '\n'.join(lines)

# 2. Fix column type typo VARCHAR(500)e -> VARCHAR(500)
if 'VARCHAR(500)e' in text:
    text = text.replace('VARCHAR(500)e', 'VARCHAR(500)')
    print('Fixed VARCHAR(500)e typo')

p.write_text(text, encoding='utf-8')

# 3. Verify: print last 8 lines
lines2 = p.read_text(encoding='utf-8').splitlines()
print('Total lines:', len(lines2))
print('Fixed building rows:', fixed)
for l in lines2[-8:]:
    print(repr(l))
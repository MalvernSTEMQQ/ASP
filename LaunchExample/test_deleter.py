import os

for (dirname, dirs, files) in os.walk('.'):
    for filename in files:
##        print(dirname, filename)
        if 'conflicted copy' in filename:
            print(filename)
            os.remove(dirname + '\\' + filename)
            

#!/usr/bin/python3
import os
import time
import signal
import sys
import re
import threading
from http.server import HTTPServer, BaseHTTPRequestHandler, SimpleHTTPRequestHandler

splts=""
def splitencode(s):
	global splts
	while len(s)>40 :
		firstpart, secondpart = s[:len(s)//2], s[len(s)//2:]
		#print(firstpart+" "+secondpart)
		splitencode(firstpart)
		splitencode(secondpart)
		return
	#if enc==1:
	s=encode("test",s)
	splts+=s

def encode(key, string):
	encoded_chars = []
	for i in range(len(string)):
		key_c = key[i % len(key)]
		encoded_c = chr(ord(string[i]) + ord(key_c) % 256)
		encoded_chars.append(encoded_c)
	encoded_string = ''.join(encoded_chars)
	return encoded_string

def decode(key, string):
	encoded_chars = []
	for i in range(len(string)):
		key_c = key[i % len(key)]
		encoded_c = chr((ord(string[i]) - ord(key_c) + 256) % 256)
		encoded_chars.append(encoded_c)
	encoded_string = ''.join(encoded_chars)
	return encoded_string
 
def start_server():
	print('Starting server:')
	files_dir=os.path.join(os.path.dirname(__file__),'Files')
	os.chdir(files_dir)
	httpd.serve_forever()

def start_snort():
	print('Starting snort:')
	os.system(createfile)

def files_create():
	print('Starting filemodification:')
	global splts
	while True:
		file = open("/home/vm/Documents/Server/snortlog", "r")
		allines=file.readlines()
		file.close()

		warnfile = open ("/home/vm/Documents/Server/logwarn", "w")
		logfile = open ("/home/vm/Documents/Server/logfile", "w")

		warnfilenc = open ("/home/vm/Documents/Server/Files/warnfilenc.txt", "w")
		logfilenc = open ("/home/vm/Documents/Server/Files/logfilenc.txt", "w")
		serfilenc = open ("/home/vm/Documents/Server/Files/serfilenc.txt", "w")

		k=0
		l=0
		lines = ""
		lineserious=""
		for line in allines:
			regex=re.search('Priority: (\d+)',line)
			if int(regex.group(1))<=2 :
				l+=1
				#lineserious+=line
				#lineserious+="nexterror"

			k+=1
			lines+=line
			lines+="nexterror"

		lineserious="Number of SERIOUS WARNINGS : "+str(l)+ "    "

		lines2="Number of WARNINGS : "+str(k)+ "    nexterror" + lines

		if k>=2 :		
			lines1="Number of WARNINGS : "+str(k)+ "    nexterror" + allines[k-1] +"nexterror"+ allines[k-2]
		else :
			lines1="Number of WARNINGS : "+str(k)+ "    nexterror"

		warnfile.write(lines1)
		logfile.write(lines2)

		serfilenc.write(encode("test",lineserious))
		warnfilenc.write(encode("test",lines1))

		splitencode(lines2)
		logfilenc.write(splts)
		splts=""

		#encode("test",lines1)
		serfilenc.close()
		warnfilenc.close()
		logfilenc.close()


		warnfile.close()
		logfile.close()
		time.sleep(5)

t = threading.Thread(target=start_server)
t2 = threading.Thread(target=start_snort)
t3 = threading.Thread(target=files_create)
httpd = HTTPServer(('192.168.1.25', 4448), SimpleHTTPRequestHandler)

def signal_handler(sig, frame):
    print('You pressed Ctrl+C!\nSnort & Server Ends')
    os.system(stopservsnort)
    httpd.shutdown()
    t2.join()
    t.join()
    t3.join()


createfile="sudo /usr/bin/stdbuf -oL snort -A console -q -u snort -g snort -c /etc/snort/snort.conf -i enp0s3 > /home/vm/Documents/Server/snortlog"
stopservsnort="sudo killall snort | sudo killall /usr/bin/python3"
os.system("> /home/vm/Documents/Server/snortlog")
os.system("> /home/vm/Documents/Server/Files/logfilenc.txt")
os.system("> /home/vm/Documents/Server/Files/serfilenc.txt")
os.system("> /home/vm/Documents/Server/Files/warnfilenc.txt")
os.system("> /home/vm/Documents/Server/logfile")
os.system("> /home/vm/Documents/Server/logwarn")
time.sleep(3)
t3.start()
t2.start()
t.start()

signal.signal(signal.SIGINT, signal_handler)





	



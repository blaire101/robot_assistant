import sys
sys.path.append("./")
import constant_mail
import os, smtplib, mimetypes, base64
from email.mime.text import MIMEText

## about var
mail_to_list = constant_mail.mail_to_list
mail_host = constant_mail.mail_host
mail_user = constant_mail.mail_user
mail_user_address = constant_mail.mail_user_address
mail_password = constant_mail.mail_password
mail_postfix = constant_mail.mail_postfix
mail_from = constant_mail.mail_from
mail_subject = constant_mail.subject
mail_type = constant_mail.mail_type

def get_mail_to_list(mailto) :
    to_list = mailto.split(",")
    for i in range(0, len(to_list)):
        to_list[i] = to_list[i].strip()+"@" + mail_postfix
    return to_list

def send_mail(mail_to_list, subject, content, mail_type):
    me = mail_from
    message = MIMEText(content, _subtype=mail_type, _charset="utf-8")
    message["Subject"] = subject
    message["From"] = me
    message["To"] = ';'.join(mail_to_list)

    try:
        server = smtplib.SMTP()
        server.connect(mail_host)
        server.login(mail_user_address, base64.b64decode(mail_password))
        server.sendmail(me, mail_to_list, message.as_string())
        server.close()
        return True
    except Exception, e:
        print >> sys.stderr, "[ERROR]: %s" % str(e)
        sys.exit(-1)

def main():
    print len(sys.argv)
    if len(sys.argv) < 4 :
        print >> sys.stderr, "args count : " + str(len(sys.argv)) + " is error!"
        return
    mail_subject = sys.argv[1]
    content=sys.argv[2]
    mail_to_list = get_mail_to_list(sys.argv[3])
#    print mail_to_list
#   mail_type='plain'
#    print mail_subject,content,mail_to_list
    if send_mail(mail_to_list, mail_subject, content, mail_type):
        print >> sys.stdout, "[INFO] Email Auto Send Success!"
    else:
        print >> sys.stderr, "[ERROR] Email Auto Send Failure!"

if __name__ == '__main__':
    main()

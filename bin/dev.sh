Monitor network traffic bandwidth:
sudo iftop -i eth0 -B -f "port 57463 or port 57464"

Take Jenkins backup from server:
zip -r /home/ubuntu/jenkins_backup.zip /var/lib/jenkins/jobs

Move backup to PC:
scp -i ~/.ssh/orlof.pem ubuntu@ec2-54-194-67-31.eu-west-1.compute.amazonaws.com:jenkins_backup.zip .


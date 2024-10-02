rm -rf /Users/hgw/work/project/DexAPI/*.zip
cd /Users/hgw/work/project
zip -r DexAPI.zip DexAPI -x "*.idea*" -x "*.zip*" -x "*node_modules*"
mv /Users/hgw/work/project/DexAPI.zip /Users/hgw/work/project/DexAPI/DexAPI.zip
sudo scp -i /Users/hgw/work/remote/AWS_Vir1.pem /Users/hgw/work/project/DexAPI/DexAPI.zip ubuntu@54.156.110.67:/home/ubuntu/upload/DexAPI.zip

cd terraform/
terraform init
terraform apply -target=aws_db_instance.sitemap-news-manager-db --auto-approve
terraform apply -target=aws_ecr_repository.sitemap-news-manager-ecr --auto-approve

cd ..
~/.local/bin/aws ecr get-login-password --region eu-west-3 | docker login --username AWS --password-stdin 379176754268.dkr.ecr.eu-west-3.amazonaws.com

export DB_HOST=`~/.local/bin/aws rds --region eu-west-3 describe-db-instances --query "DBInstances[0].Endpoint.Address" | tr -d '"'`
~/./../jenkins_home/.sdkman/candidates/maven/current/bin/mvn clean install

docker build . -t sitemap-news-manager-ecr --build-arg db_host=`~/.local/bin/aws rds --region eu-west-3 describe-db-instances --query "DBInstances[0].Endpoint.Address" | tr -d '"'`
docker tag sitemap-news-manager-ecr:latest 379176754268.dkr.ecr.eu-west-3.amazonaws.com/sitemap-news-manager-ecr:latest
docker push 379176754268.dkr.ecr.eu-west-3.amazonaws.com/sitemap-news-manager-ecr:latest

cd terraform/
terraform apply --auto-approve

sleep 120s

v=`~/.local/bin/aws ecs list-tasks --cluster "default" --output text --query taskArns[0]`
if  [ $v != "None" ]; then
    ~/.local/bin/aws ecs stop-task --task $v > stop.txt
fi
~/.local/bin/aws ecs run-task --cluster "default" --task-definition sitemap-news-manager-task > run.txt
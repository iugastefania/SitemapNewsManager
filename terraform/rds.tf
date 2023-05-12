resource "aws_db_instance" "sitemap-news-manager-db" {
  allocated_storage    = 20
  engine               = "mysql"
  db_name              = "news"
  engine_version       = "8.0.28"
  instance_class       = "db.t3.micro"
  identifier           = "sitemap-news-manager-db-2"
  username             = "admin"
  password             = "admin123"
  vpc_security_group_ids = ["sg-0312b7665e7e61b3f"]
  publicly_accessible   = true
  skip_final_snapshot  = true
}
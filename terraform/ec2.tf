resource "aws_instance" "sitemap-news-manager-ec2" {
  ami           = "ami-089e1275464c272ba"
  instance_type = "t2.micro"
  key_name = "sitemapnewsmanager"
  vpc_security_group_ids = ["sg-07d54d82ff526e75a"]
  iam_instance_profile = aws_iam_instance_profile.ec2-profile.name
}

resource "aws_iam_instance_profile" "ec2-profile" {
  name = "ec2-profile-2"
  role = aws_iam_role.ec2-role.name
}
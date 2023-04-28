resource "aws_instance" "sitemap-news-manager-ec2" {
  ami           = "ami-0f51be0cf2e87b06c"
  instance_type = "t2.micro"
  key_name = "emp"
  vpc_security_group_ids = ["sg-02c8a7439bd6661ae"]
  iam_instance_profile = aws_iam_instance_profile.ec2-profile.name
}

resource "aws_iam_instance_profile" "ec2-profile" {
  name = "ec2-profile-2"
  role = aws_iam_role.ec2-role.name
}
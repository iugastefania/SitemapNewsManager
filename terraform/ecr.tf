resource "aws_ecr_repository" "sitemap-news-manager-ecr" {
  name                 = "sitemap-news-manager-ecr"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}
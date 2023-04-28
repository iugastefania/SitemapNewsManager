resource "aws_ecr_repository" "sitemap-news-manager-ecr" {
  name                 = "sitemap-news-manager-ecr-2"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}
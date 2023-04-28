resource "aws_ecs_task_definition" "ecs-task-definition" {
  family = "sitemap-news-manager-task-2"
  requires_compatibilities = ["EC2"]

  container_definitions = jsonencode([
    {
      name      = "sitemap-news-manager-container"
      image     = aws_ecr_repository.sitemap-news-manager-ecr.repository_url
      memory    = 500
      essential = true
      portMappings = [
        {
          containerPort = 8080
          hostPort      = 8080
        }
      ]
    }
  ])
}
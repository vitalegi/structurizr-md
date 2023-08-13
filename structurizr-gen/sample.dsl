workspace "Amazon Web Services Example" "An example AWS deployment architecture." {

    model {
        springPetClinic = softwaresystem "Spring PetClinic" "Allows employees to view and manage information regarding the veterinarians, the clients, and their pets." {
            webApplication = container "Web Application" "Allows employees to view and manage information regarding the veterinarians, the clients, and their pets." "Java and Spring Boot" {
                tags "Application"
                c1 = component "Web Service 1"
                c2 = component "Web Service 2"
            }
            database = container "Database" "Stores information regarding the veterinarians, the clients, and their pets." "Relational database schema" {
                tags "Database"
            }
        }

        app2 = softwaresystem "App 2" {
            webApplication2 = container "Web Application 2: []()%$&à\èéìù/" {
                tags "Application"
                c3 = component "Web Service 3"
                c4 = component "Web Service 4"
            }
            database2 = container "Database" {
                tags "Database"
            }
            container "test_:%<>|?"
        }

        app3 = softwaresystem "App 3" {
            webApplication3 = container "Web Application 3" {
                tags "Application"
            }
        }

        app4 = softwaresystem "App 4" {
        }
        softwaresystem "App 5()[]!!: []()%$&à\èéìù/" {
        }

        c1 -> database "Reads from and writes to" "MySQL Protocol/SSL"
        c2 -> c1
        c3 -> database
        c3 -> c4
        c4 -> c2
        c4 -> c1 Test

        live = deploymentEnvironment "Live" {

            deploymentNode "Amazon Web Services" {
                tags "Amazon Web Services - Cloud"

                region = deploymentNode "US-East-1" {
                    tags "Amazon Web Services - Region"

                    route53 = infrastructureNode "Route 53" {
                        description "Highly available and scalable cloud DNS service."
                        tags "Amazon Web Services - Route 53"
                    }

                    elb = infrastructureNode "Elastic Load Balancer" {
                        description "Automatically distributes incoming application traffic."
                        tags "Amazon Web Services - Elastic Load Balancing"
                    }

                    deploymentNode "Autoscaling group" {
                        tags "Amazon Web Services - Auto Scaling"

                        deploymentNode "Amazon EC2" {
                            tags "Amazon Web Services - EC2"

                            webApplicationInstance = containerInstance webApplication
                        }
                    }

                    deploymentNode "Amazon RDS" {
                        tags "Amazon Web Services - RDS"

                        deploymentNode "MySQL" {
                            tags "Amazon Web Services - RDS MySQL instance"

                            databaseInstance = containerInstance database
                        }
                    }

                }
            }

            route53 -> elb "Forwards requests to" "HTTPS"
            elb -> webApplicationInstance "Forwards requests to" "HTTPS"
        }
    }

    views {
        systemLandscape landscape {
            include *
            autoLayout
        }

        deployment springPetClinic "Live" "AmazonWebServicesDeployment" {
            include *
            autolayout lr

            animation {
                route53
                elb
                webApplicationInstance
                databaseInstance
            }
        }

        styles {
            element "Element" {
                shape roundedbox
                background #ffffff
            }
            element "Container" {
                background #ffffff
            }
            element "Application" {
                background #ffffff
            }
            element "Database" {
                shape cylinder
            }
        }

        themes https://static.structurizr.com/themes/amazon-web-services-2020.04.30/theme.json
    }

}
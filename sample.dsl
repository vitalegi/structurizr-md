workspace "Amazon Web Services Example" "An example AWS deployment architecture." {

    model {
        user = person "User"

        springPetClinic = softwaresystem "Spring PetClinic" "Allows employees to view and manage information regarding the veterinarians, the clients, and their pets." {
            webApplication = container "Web Application" "Allows employees to view and manage information regarding the veterinarians, the clients, and their pets." "Java and Spring Boot" {
                tags "Application"
                c1 = component "Web Service 1"
                c2 = component "Web Service 2"
            }
            database = container "Database" "Stores information regarding the veterinarians, the clients, and their pets." "Relational database schema" {
                tags "Database"
            }
            c1 -> database "Reads from and writes to" "MySQL Protocol/SSL"
            c2 -> c1
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
            c3 -> database2
            c3 -> c4
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

        c4 -> c2 "test 1"
        c4 -> c1 "test 2"

        user -> c2 "Access to user data"

        live = deploymentEnvironment "Live" {

            deploymentNode "Amazon Web Services" {
                tags "Amazon Web Services - Cloud"

                region = deploymentNode "US-East-1" {
                    tags "Amazon Web Services - Region"

                    route53 = infrastructureNode "Route 53" {
                        description "Highly available and scalable cloud DNS service."
                        tags "Amazon Web Services - Route 53"
                    }

                    infrastructureNode "s3://my-bucket" {
                        tags "S3 Bucket"
                    }

                    infrastructureNode "Athena 1" {
                        tags "Athena"
                    }

                    elb = infrastructureNode "Elastic Load Balancer" {
                        description "Automatically distributes incoming application traffic."
                        tags "Amazon Web Services - Elastic Load Balancing"
                    }

                    deploymentNode "Autoscaling group" {
                        tags "Amazon Web Services - Auto Scaling"
                        properties {
                            min 1
                            max 10
                            subnet 10.123.234.0
                        }

                        deploymentNode "Amazon EC2" {
                            tags "Amazon Web Services - EC2"

                            webApplicationInstance = containerInstance webApplication
                        }
                    }

                    deploymentNode "Amazon RDS" {
                        tags "Amazon Web Services - RDS"

                        deploymentNode "Oracle 11g" {
                            tags "Oracle"

                            databaseInstance = containerInstance database
                        }
                    }

                }
            }

            route53 -> elb "Forwards requests to" "HTTPS" {
                properties {
                    port 443
                }
            }
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

        deployment * "Live" "live" {
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
                #background #B9D6F2
            }
            element "Component" {
                background #003559
            }
            element "Container" {
                background #006DAA
            }
            element "Software System" {
                background #0353A4
            }
            element "Database" {
                shape cylinder
            }
            # [IMG_1]/[IMG_2] referencing a remote puml
            element "S3 Bucket" {
                properties {
                    c4plantuml.sprite SimpleStorageServiceBucket
                    stroke #7AA116
                }
            }
            # [IMG_3] referencing a local file
            element "Oracle" {
                properties {
                    c4plantuml.sprite oracle
                }
            }
            # [IMG_4] referencing a remote file directly
            element "Athena" {
              stroke "#8C4FFF"
              icon https://raw.githubusercontent.com/awslabs/aws-icons-for-plantuml/main/dist/Analytics/Athena.png
            }
        }
        properties {
            c4plantuml.tags true
            c4plantuml.legend false
            c4plantuml.elementProperties true
            c4plantuml.relationshipProperties true

            # including remote resources manually
            #plantuml.defines "aws https://raw.githubusercontent.com/awslabs/aws-icons-for-plantuml/v18.0/dist"
            #plantuml.includes "aws/AWSCommon.puml, aws/Storage/SimpleStorageServiceBucket.puml, theme/oracle.puml"

            # referencing stdlib
            plantuml.includes "<awslib/AWSCommon>, <awslib/Storage/SimpleStorageServiceBucket>, theme/oracle.puml"

            # [IMG_3] referencing a local file
            # plantuml.includes "theme/oracle.puml"
        }
        #theme https://static.structurizr.com/themes/google-cloud-platform-v1.5/theme.json
    }

}
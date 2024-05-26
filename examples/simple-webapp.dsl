workspace "Reader WebApp Example" "An example deployment architecture." {

    model {
        user = person "User"
        sysadmin = person "Sysadmin"

        reader_ss = softwaresystem "Reader" {
            description "Allows users to access the reader"
        }

        !extend reader_ss {
            reader_frontend = container "Single Page Application" {
                description "Allows users to use reader functionalities."
                technology "Vue 3"
            }
            reader_backend = container "Backend" {
                description "Business Logic of the reader"
                technology "Spring Boot 3, Java 21"
            }
            reader_database = container "RDBMS" {
                description "Stores books informations"
                technology "Postgres"
            }
        }

        aws_ss = softwaresystem "Amazon Web Services" {
            aws_s3_bucket_reader = container "AWS S3 Bucket" {
                description "Stores books"
            }
        }

        user -> reader_frontend "Uses"
        sysadmin -> reader_database "Access"

        reader_frontend -> reader_backend "Uses"

        reader_backend -> reader_database "Reads from and writes to" "Postgres Protocol/SSL"
        reader_backend -> aws_s3_bucket_reader "Upload books"
        reader_frontend -> aws_s3_bucket_reader "Download books"

        live = deploymentEnvironment "Live" {
            deploymentNode "Netlify" {
                deploymentNode "app.netlify.app" {
                    properties {
                        port 443
                    }
                    live_netlify_reverse_proxy_backend = infrastructureNode "Reverse Proxy" {
                    }
                    live_reader_frontend_instance = containerInstance reader_frontend
                }
            }
            deploymentNode "fly.io" {
                deploymentNode "app.fly.io" {
                    properties {
                        port 443
                        scale 0..1
                    }
                    deploymentNode "Application Server" {
                        properties {
                            machine shared-1x-cpu
                            memory 512MB
                            "java version" 21
                        }
                        live_reader_backend_instance = containerInstance reader_backend
                    }
                }

                deploymentNode "app.fly.db" {
                    live_reader_database_instance = containerInstance reader_database
                    tags "Postgres"
                    properties {
                        machine shared-1x-cpu@256MB
                        scale 1
                        "postgres version" 15
                    }
                }
            }
            live_netlify_reverse_proxy_backend -> live_reader_backend_instance "HTTP Forward" "Rewrites /api as /"

            deploymentNode "Amazon Web Services" {
                tags "Amazon Web Services - Cloud"

                region = deploymentNode "US-East-1" {
                    tags "Amazon Web Services - Region"

                    deploymentNode "s3://my-bucket-02" {
                        tags "S3 Bucket"
                        properties {
                            url "s3://my-bucket-02"
                        }
                        live_aws_s3_bucket_reader_instance = containerInstance aws_s3_bucket_reader
                    }
                }
            }
        }

        local = deploymentEnvironment "Local" {
            deploymentNode "NGinx" {
                properties {
                    port 9000
                }
                local_reverse_proxy_backend = infrastructureNode "Reverse Proxy" {
                }
                local_reader_frontend_instance = containerInstance reader_frontend
            }
            deploymentNode "Application Server" {
                properties {
                    port 8080
                    "java version" 21
                }
                local_reader_backend_instance = containerInstance reader_backend
            }

            deploymentNode "Database" {
                local_reader_database_instance = containerInstance reader_database
                tags "Postgres"
                properties {
                    "postgres version" 15
                }
            }

            local_reverse_proxy_backend -> local_reader_backend_instance "HTTP Forward" "HTTP Forward" "Rewrites /api as /"

            deploymentNode "Amazon Web Services" {
                tags "Amazon Web Services - Cloud"

                deploymentNode "US-East-1" {
                    tags "Amazon Web Services - Region"

                    deploymentNode "my-bucket-01" {
                        tags "S3 Bucket"
                        properties {
                            url "s3://my-bucket-01"
                        }
                        containerInstance aws_s3_bucket_reader
                    }
                }
            }
        }
    }

    views {
        systemLandscape landscape {
            include *
            autoLayout
        }

        deployment reader_ss "Live" "reader_deployment" {
            include *
            autolayout lr
        }

        deployment * "Live" "live" {
            include *
            autolayout lr
        }

        deployment * "Local" "local" {
            include *
            autolayout lr
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
            plantuml.includes "<awslib/AWSCommon>, <awslib/Storage/SimpleStorageServiceBucket>"

            # [IMG_3] referencing a local file
            # plantuml.includes "theme/oracle.puml"
        }
        #theme https://static.structurizr.com/themes/google-cloud-platform-v1.5/theme.json
    }

}
workspace "Multiple Entrypoints Example" "An example deployment architecture." {

    model {
        user = person "User"
        admin = person "Admin"

        system1 = softwaresystem "System 1" {
            basic_frontend = container "Single Page Application" {
            }
            admin_frontend = container "Administrative pages" {
            }
            backend = container "Backend" {
                basic_api = component "Basic API"
                admin_api = component "Admin API"
            }
            database = container "RDBMS" {
            }
        }

        user -> basic_frontend "Uses"
        admin -> admin_frontend "Uses"

        basic_frontend -> backend "Forwards" {
            tags "LogicConnection"
        }
        admin_frontend -> backend "Uses" {
            tags "LogicConnection"
        }

        live = deploymentEnvironment "Live" {
            deploymentNode "basic.website.com" {
                properties {
                    port 443
                }
                live_basic_frontend_instance = containerInstance basic_frontend
            }
            deploymentNode "admin.website.com" {
                properties {
                    port 443
                }
                live_admin_frontend_instance = containerInstance admin_frontend
            }
            deploymentNode "Nginx cluster" {
                backend_nginx = infrastructureNode "backend.internal.website.com" {
                }
            }
            deploymentNode "backend.internal.k8s.website.com" {
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
                    live_backend_instance = containerInstance backend
                }
            }
            live_basic_frontend_instance -> backend_nginx
            live_admin_frontend_instance -> backend_nginx
            backend_nginx -> live_backend_instance
        }
    }

    views {
        systemLandscape landscape {
            include *
            autoLayout
        }

        deployment system1 "Live" "system1_deployment" {
            include *
            autolayout lr
        }

        deployment * "Live" "live" {
            include *
            autolayout lr
            exclude relationship.tag==LogicConnection
        }

        styles {
            element "Element" {
                shape roundedbox
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
        }
        properties {
            c4plantuml.tags true
            c4plantuml.legend false
            c4plantuml.elementProperties true
            c4plantuml.relationshipProperties true
        }
    }

}
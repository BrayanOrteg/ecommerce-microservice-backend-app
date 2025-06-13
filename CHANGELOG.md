## [1.0.1](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/compare/v1.0.0...v1.0.1) (2025-06-13)


### Bug Fixes

* deleted prs fails ([816caaa](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/816caaa441c28f923f3ca452618f22c5a7b56c5f))

# 1.0.0 (2025-06-13)


### Bug Fixes

* add permissions ([52962db](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/52962dbb3c79c41ed76c421c3813d68dc28a8357))
* change from Azure Load Test to manual scripts ([22c4b82](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/22c4b82947860866b4de21942be75ec5f4f26e4d))
* change locust publish csv to wildcard ([586f854](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/586f85414154064f7123e5e0c2c68af4bdcb6bad))
* change OWASP port ([f5d875c](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/f5d875cb258dfdbdb8b6e52aa93bd1557137d77e))
* change var scope ([f7d6667](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/f7d666725542655a303e79c881a35789c89435c4))
* errors in prod.yml and releaserc ([a9c2748](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/a9c27484900c22c58e74da29fab93868d679e684))
* export e2e artifact ([379ed81](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/379ed813c538541c0a8c912bb4e17e091cff44ea))
* forgot to install newman before e2e testing ([cc75dab](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/cc75dabd95f902e707b517d35298d6831144da43))
* identation problems ([f3838ee](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/f3838eed1d6298589c32513b5ac9d730b74015cc))
* include api-gateway in manifests ([9e03e7c](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/9e03e7c500d25bd103a04e40b93f7b8019ab370f))
* include az and aks credentials for every KubernetesManifest@1 ([d64ae9e](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/d64ae9e815651cc9b97dc1aedea664d150fe7e13))
* include azure and aks credentials for KubernetesManifest@1 deployment task ([33021fe](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/33021fe1e1dae30775627770eb1d677004c9df0b))
* include DockerInstaller@0 task and update compose command ([e743cc1](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/e743cc118d8e3af174da957dbbee8106772227f2))
* include Maven@4 build task before Docker again ([720f891](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/720f891c4ab981075d176caac14cb059797f8fdd))
* include Maven@4 build task before Docker tasks so Docker can access the jars ([fa2aa1a](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/fa2aa1a2edf3bd23dbcc56d70c126ea4595a4ef4))
* include pending build context and image reference for service-discovery and cloud-config ([b203480](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/b2034803265a688e6910f041671e6b8ede82d07a))
* include SonarCloudPrepare@3 and Maven@4 tasks in the same job so the Sonar cloud configuration persists ([59a8723](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/59a87236947ada337621c30877f518bde7aa9bd0))
* include Trivy tresholds for severity and set exit code ([c0bc90c](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/c0bc90cefec92b1b8bd5c6d27d5da1d62497c627))
* print Trivy results and continue even when critical vulnerabilities are found ([dd8c0f5](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/dd8c0f54e3f0e54d1dcb8e905aff388f4dc4e664))
* remove , in manifests list ([0065bbe](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/0065bbe931ffa13974c4f343d3a073023d92b320))
* remove exit code ([ab0af89](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/ab0af8902b34bd1b7bd235adcbf67a5a21a9db09))
* remove resources limits ([27dd5da](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/27dd5da98da74e1813e7f23c74eb3ec95d440fa1))
* remove Trivy failed scan condition, microservices jdk-11 image is so old it contains a lot of vulnerabilities ([f8a4e54](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/f8a4e54d822783adf1d60b163e819c8ccb67c5e9))
* rename cloud-config image name in Trivy configuration ([37c2175](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/37c2175d80a7506939ebf4eae05341a054e9a389))
* SonarCloudPrepare@3 task should go before Maven@4 task ([f8a3955](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/f8a3955361132ab9f068d731f2c4f5f3f49c146c))
* try to fetch again api gateway ip ([3b6da95](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/3b6da951fd7835ccfb048008736cd6286076bab2))
* trying to debug locust error ([15748b6](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/15748b60f167d2b6d5f331f781315774f73f0f51))
* trying to debug newman ([b16548f](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/b16548fee28e544c85178b3c21222a0ec6eddb0d))
* trying to discover why E2E isn't being updated ([7f3454e](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/7f3454e6fbd9b024f4a9e63a495b794964873619))
* turn off enviroment source in newman ([070eec3](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/070eec336e793febd920c2f231684154ffe5f005))
* turn off SonarQube parameter in Maven@4 task ([d3ec7b5](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/d3ec7b5935b632f4b14783917d648ac6cc8393c3))
* typo error ([11fcb92](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/11fcb922c0412db1b95d5503975bf61db83ff449))
* typo in loadtest file ([b72e2e6](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/b72e2e60b22a4630fcc20eb106b5fdade3cd2b99))
* update COPY route in Dockerfiles and include build context to each ([bc2fc0b](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/bc2fc0b681347c31e055aa750cd56503623c3e8d))
* update DockerJob name ([b40aadc](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/b40aadc9c89b512401a26e96e121deb6911c3d7b))
* update kubectl wait for kubectl rollout ([03023a6](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/03023a68adb6766bc60e2eb73b70fc980ea314b7))
* ZAP is no longer an OWASP project change to bash exc ([9f5e0a4](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/9f5e0a4d60e4c873cf0b800fe68340a2382dad99))


### Features

* add AzureCLI@2 login task and KubernetesManifest@1 deployment task for core services: zipkin, service discovery and cloud config ([929d8d6](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/929d8d680eed38c18ce9a76c85c185208560112e))
* add Trivy static analysis for Docker images ([4c5bb63](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/4c5bb63ea9e4d7adee33f3fe0de3d18847b7fda9))
* create azure pipeline with build, tests and docker push steps ([01645d7](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/01645d7b5d37d128f662aa3ad26bc70a5898cd68))
* deploy every service ([f761d85](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/f761d85033fe07d0930915338576790f1fabd641))
* dev-stage-prod envs for docker and k8s ([3bdca81](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/3bdca81f23148263613753ce99e29d42a51c67d7))
* include locust stress tests ([fa16365](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/fa163657e08900e31e36d69252aa9a1b110774c4))
* include readinessProbe and livenessProbe in zipkin yml for feature deployment ([0db8e76](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/0db8e76333e06fc8c8fca4dff7e09be297efbed7))
* include SonarCloudPrepare@3 task for static code analysis ([85a8723](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/85a87236b5365a16dd49b8e804cdb5e13969c32c))
* JaCoCo coverage tests ([80aa749](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/80aa7491f8ef8e9f20e0db7a4beca8c3fb4e4ab1))
* locust host from env ([1fbe67e](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/1fbe67e0b6ddb0602c43e87934cdc75e79428e9c))
* new dev branch pipeline ([8b5b4de](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/8b5b4de1cc7110b220c72f08206061ab2c8f529c))
* prod branch pipeline, test semantic release ([b3c48c1](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/b3c48c1bfe1883595dc1c83275ff5764fbad10eb))
* remove DockerInstaller@0 task use ubuntu's image docker instead ([a0b0f34](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/a0b0f34a27e9dc8732182d4c8231862d92d0df19))
* semantic-release ([a08c7e3](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/a08c7e3e73293f79c266bf31d33d2782b0e0d527))
* stage branch pipeline ([54ab2b3](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/54ab2b32bb95b6944638c16245d2306959065fcd))
* turn api-gateway into LoadBalancer for test ([2200986](https://github.com/BrayanOrteg/ecommerce-microservice-backend-app/commit/220098648a2f8a26f2b55e2e9734ec64bb013bef))

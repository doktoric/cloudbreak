include integcb/Environment

ifeq ($(REPO_URL),"")
REPO_URL = https://cloudbreak-maven.s3.amazonaws.com/releases
endif

all: download-cbd buildcb runtest

runtest: create-image create-cloudbreak-context docker-compose stop-containers check-results

delete-and-run: download-cbd cbd-delete buildcb runtest

without-build: download-cbd runtest

build-with-docker: download-cbd docker-build runtest

download-jar-from-s3:
	./scripts/download-artifacts.sh

download-cbd:
	./scripts/download-cbd.sh

buildcb:
	./scripts/build-cb.sh

create-image:
	./scripts/create-image.sh

create-cloudbreak-context:
	./scripts/create-cloudbreak-context.sh

docker-compose:
	./scripts/docker-compose.sh

check-results:
	./scripts/check-results.sh

stop-containers:
	./scripts/stop-containers.sh

remove-test-containers:
	./scripts/remove-test-containers.sh

docker-build:
	./scripts/docker-build.sh

revert-db:
	./scripts/revert-db.sh

cbd-delete:
	./scripts/cbd-delete.sh

upload-s3:
	aws s3 cp ./apidefinitions/new.json s3://cloudbreak-swagger/swagger-$(VERSION).json --acl public-read

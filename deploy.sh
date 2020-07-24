git branch -D deploy &>/dev/null
git checkout -b deploy
mv .gitignore_deploy .gitignore
cp resources/service.json resources/_service.json
cp resources/credentials.json resources/_credentials.json
git add resources/service.json
git add resources/credentials.json
git commit -am "deploy"
git push heroku deploy:master --force
git checkout master
mv resources/_service.json resources/service.json
mv resources/_credentials.json resources/credentials.json

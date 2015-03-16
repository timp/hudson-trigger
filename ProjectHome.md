A web app which consumes a POST request, say from a googlecode subversion commit, and triggers a list of Hudson builds, having added authentication.

Ensure that you do not have _Prevent Cross Site Request Forgery exploits_ in your Hudson configuration checked.

Checkout source.
Edit src/main/filtered/net/pizey/hudson/trigger/net.pizey.hudson.triggerTrigger.properties

Add a list of token=url1,url2,url3 pairs.

Do not hard code user or password.

For each Hudson project enable remote triggering and place token in the token field (not sure this is required).

Build war file with
```
mvn clean install -Duser=foo -Dpassword=bar 
```
drop war into servlet container, so that url will be something like
http://paneris.net:8080/trigger/Trigger?token=mytoken

Place the url http://paneris.net:8080/trigger/Trigger?token=mytoken into the googlecode adminsource post commit url field.






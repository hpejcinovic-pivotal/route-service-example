#! /bin/bash

securityServiceUrl=https://security-service.bosh-lite.com
spaceName=dev

spaceGuid=$(cf space $spaceName --guid)
echo 'spaceGuid:'$spaceGuid
#$cf curl /v2/user_provided_service_instances -X POST -d '{"space_guid":"","name":"my-upsi","route_service_url":"https://my-route-service.cf.example.com"}'
#####Step3
echo "1:"
paramForUserProvidedInstance=\'{'"space_guid"':\"$spaceGuid\",'"name":"my-upsi"','"route_service_url"':\"$securityServiceUrl\"}\'
echo 'paramForUserProvidedInstance:' $paramForUserProvidedInstance

userProvidedInstanceCommand="cf curl /v2/user_provided_service_instances -X POST -d ${paramForUserProvidedInstance}"
echo 'userProvidedInstanceCommand:' $userProvidedInstanceCommand
${userProvidedInstanceCommand}

echo "2:"
####Step5 
appGuid = $(cf curl "/v2/routes?q=host:rest-service" |  jq -r ".resources[].metadata.guid")
echo 'appGuid:' $appGuid

####Step 6
# cf curl /v2/user_provided_service_instances/your-instance-guid/routes/your-route-guid -X PUT
# cf curl /v2/user_provided_service_instances/your-instance-guid/routes/$appGuid -X PUT


cf curl  /v2/user_provided_service_instances   #guid:27cd9164-797c-43f7-823a-b04aac6241ae
cf curl "/v2/routes?q=host:rest-service"       #appGuid: 6f22473f-8618-40d7-af56-d57a05835708


###works
cf curl /v2/user_provided_service_instances/27cd9164-797c-43f7-823a-b04aac6241ae/routes/6f22473f-8618-40d7-af56-d57a05835708 -X PUT

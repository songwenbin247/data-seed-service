Steps for configuring ACS service : 
*Note* : create a ACS service from predix.io. 

1. Confirm if the client_id has password grant and acs policy scopes on the UAA .(Refer to predix-io set up the scopes)
	verify the following for the client id or set these .
	
 	scope: acs.attributes.read acs.attributes.write acs.policies.read acs.policies.write openid
 	authorized_grant_types: authorization_code client_credentials password refresh_token
	 
2. Get token from UAA for that client_id credentials.

3. Add a policy admin user 
uaac user add app_admin_1 -p app_admin_1 --emails app_admin_1@gegrctest.com

4. Add ACS admin access for app_admin_1
 uaac member add acs.policies.read app_admin_1 
 uaac member add acs.policies.write app_admin_1
 uaac member add acs.attributes.read app_admin_1 
 uaac member add acs.attributes.write app_admin_1
 
5.Token for UAA for this app_admin_1 to add policy with grant_type password

6.Add policy with the user credntials from step #6 

curl -X PUT https://<Your ACS URL>/v1/policy-set/rmd_ref_app_int -d@ ./rmd_app_policy.json -v 
-H "Authorization: Bearer <TOKEN_FROM_PREVIOUS_CMD>"
-H "Content-Type: application/json" 
-H "Predix-Zone-Id: <VCAPS ZONE ID>" 

7.Creating attributes for resources and subject for enforcement , as defined on the policy.

# Admin
curl -X PUT https://<Your ACS URL>/v1/subject/app_admin_1 -d@ ./app_admin_role_attribute.json -v 
-H "Authorization: Bearer <TOKEN_FROM_PREVIOUS_CMD>"
-H "Content-Type: application/json" 
-H "Predix-Zone-Id: <VCAPS ZONE ID>" 

# Operator
curl -X PUT https://<Your ACS URL>/v1/subject/app_user_1 -d@ ./app_operator_role_attribute.json -v 
-H "Authorization: Bearer <TOKEN_FROM_PREVIOUS_CMD>"
-H "Content-Type: application/json" 
-H "Predix-Zone-Id: <VCAPS ZONE ID>" 	


8. Test the setup Evaluate the policy for the user :
curl -X POST https://<Your ACS URL>/v1/policy-evaluation -d@ ./dataseed_policy_evaluation.json -v 
-H "Authorization: Bearer <TOKEN_FROM_PREVIOUS_CMD>"
-H "Content-Type: application/json" 
-H "Predix-Zone-Id: <VCAPS ZONE ID>" 






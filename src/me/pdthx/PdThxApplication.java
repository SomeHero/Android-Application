package me.pdthx;

import android.util.Log;
import me.pdthx.Responses.OrganizationResponse;
import me.pdthx.Services.PaymentServices;
import me.pdthx.Models.Organization;
import java.util.ArrayList;
import android.app.Application;

public class PdThxApplication
    extends Application
{
    private ArrayList<Organization> nonProfitsList = new ArrayList<Organization>();
    private ArrayList<Organization> organizationsList = new ArrayList<Organization>();

    @Override
    public void onCreate()
    {
        super.onCreate();
        new Thread(new Runnable() {
           public void run()
           {

               Log.v("Application", "Firing off nonprofits/organizations requests");
               ArrayList<OrganizationResponse> nonProfitListResponse = PaymentServices.getOrgs("NonProfits");
               ArrayList<OrganizationResponse> organizationResponse = PaymentServices.getOrgs("Organizations");
               Log.v("Application", "Got back from the server!");

               for(int i = 0; i < nonProfitListResponse.size(); i++)
               {
                   Organization ref = new Organization();
                   ref.setImageUri(nonProfitListResponse.get(i).MerchantImageUri);
                   ref.setName(nonProfitListResponse.get(i).Name);
                   ref.setSlogan("");
                   ref.setPreferredReceive(nonProfitListResponse.get(i).PreferredReceiveAccountId);
                   ref.setPreferredSend(nonProfitListResponse.get(i).PreferredSendAccountId);
                   ref.setInfo("");

                   nonProfitsList.add(ref);
               }

               BaseActivity.setNonProfitsList(nonProfitsList);

               for(int i = 0; i < organizationResponse.size(); i++)
               {
                   Organization ref = new Organization();
                   ref.setImageUri(organizationResponse.get(i).MerchantImageUri);
                   ref.setName(organizationResponse.get(i).Name);
                   ref.setSlogan("");
                   ref.setPreferredReceive(organizationResponse.get(i).PreferredReceiveAccountId);
                   ref.setPreferredSend(organizationResponse.get(i).PreferredSendAccountId);
                   ref.setInfo("");

                   organizationsList.add(ref);
               }

               BaseActivity.setOrganizationsList(organizationsList);
           }
        }).start();
    }

}

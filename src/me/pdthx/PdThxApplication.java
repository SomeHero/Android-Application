package me.pdthx;

import java.io.IOException;
import java.net.URL;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import java.util.Collections;
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
                try {
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
                        ref.setId(nonProfitListResponse.get(i).Id);
                        if (ref.getImageUri() != null)
                        {
                            try {
                                URL url = new URL(ref.getImageUri());
                                Bitmap bmp = BitmapFactory.decodeStream(url
                                    .openConnection().getInputStream());
                                ref.setPicture(bmp);
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        nonProfitsList.add(ref);
                    }

                    Collections.sort(nonProfitsList);
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
                        ref.setId(organizationResponse.get(i).Id);
                        if (ref.getImageUri() != null)
                        {
                            try {
                                URL url = new URL(ref.getImageUri());
                                Bitmap bmp = BitmapFactory.decodeStream(url
                                    .openConnection().getInputStream());
                                ref.setPicture(bmp);
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        organizationsList.add(ref);
                    }
                    Collections.sort(organizationsList);
                    BaseActivity.setOrganizationsList(organizationsList);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.e("Application", "Failed");
                }
            }
        }).start();
    }

}

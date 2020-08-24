package com.dialogflow.fitnessbot;

//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
//import com.yaroslav.news.json.BotResponse;
//import com.yaroslav.news.json.BotWebhook;
import com.dialogflow.fitnessbot.json.BotResponse;
import com.dialogflow.fitnessbot.json.BotWebhook;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Spring Boot Hello案例
 * <p>
 * Created by bysocket on 26/09/2017.
 */
@RestController
@RequestMapping(value = "/app")
public class AppController
{
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String SayHello()
    {
        return "Hello";
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public BotResponse GetBotResponse(@RequestBody BotWebhook i_Webhook) throws IOException
    {
        BotResponse response = new BotResponse();
        if (i_Webhook != null && i_Webhook.getQueryResult() != null
                && i_Webhook.getQueryResult().getParameters() != null)
        {
            String subject = i_Webhook.getQueryResult().getParameters().getSubject();
            if ((subject != null) && !(subject.equals("")))
            {
                response.setFulfillmentText(process(subject) );
                response.setSource("something!!");
            }
        };

        return response;
    }

    private String process(String i_Keyword) throws IOException
    {
        String res = "";
        String queryResult = doQuery(i_Keyword);
        Pattern title = Pattern.compile("meta itemprop=.itemReviewed. content=\"([A-Za-z, 0-9.]+)\">");
        Pattern image = Pattern.compile("src=\"([A-Za-z,_:/ 0-9.]+jpg)\"");
        Pattern link = Pattern.compile("href=\"([A-Za-z,-=?_:/0-9.]+)\"");
        System.out.println(">>>" + queryResult.replace("\n", ""));
        String[] options = (queryResult.replace("\n", "").split("data-bb-category=\"search\" {8}"));

        for (String option : options)
        {
            Matcher titleMatcher = title.matcher(option);
            Matcher imageMatcher = image.matcher(option);
            Matcher linkMatcher = link.matcher(option);
            if (titleMatcher.find() && imageMatcher.find() && linkMatcher.find())
            {
                String fixedImage = imageMatcher.group(1).replace("130", "300");
                res += titleMatcher.group(1) + "\n" + linkMatcher.group(1) + "<br><br>";
                System.out.println(">>>>>" + titleMatcher.group(1) + "\n" + linkMatcher.group(1) + "\n");
            }
        }

        return res;
    }

    private String doQuery(String i_Keyword) throws IOException
    {
        OkHttpClient client = new OkHttpClient();
        System.out.println("query for " + i_Keyword);
        Request request = new Request.Builder()
                .url("https://shared-search.bodybuilding.com/slp/full?context=all&query=" + i_Keyword)
                .method("GET", null).build();
//                .addHeader("Connection", "keep-alive")
//                .addHeader("Upgrade-Insecure-Requests", "1")
//                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36")
//                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
//                .addHeader("Sec-Fetch-Site", "same-origin")
//                .addHeader("Sec-Fetch-Mode", "navigate")
//                .addHeader("Sec-Fetch-User", "?1")
//                .addHeader("Sec-Fetch-Dest", "document")
//                .addHeader("Referer", "https://shared-search.bodybuilding.com/slp/full?context=all&query=protein")
//                .addHeader("Accept-Language", "en-IL,en;q=0.9,he-IL;q=0.8,he;q=0.7,ru-RU;q=0.6,ru;q=0.5,en-US;q=0.4")
//                .addHeader("Cookie", "RES_TRACKINGID=768354949466112; ResonanceSegment=1; _admrla=2.; m=7BA08BEF-69A0-4A49-8384-99F6F7E09B5D; _ga=GA1.3.926771882.1593163095; _gaexp=GAX1.2.rz5s-2yhSrymJEmKgm10gQ.18553.1; brwsr=838b34be-cbec-11ea-b62a-42010a24661e; pubconsent=BO29C0JO29C0JAJAEAENDTAB-AAACw; _gcl_au=1.1.341883615.1595435751; euconsent=BO29C0JAAAAAAAJAEAENDT-AAAAxJ7_______9_-____9uz_Ov_v_f__33e8__9v_l_7_-___u_-23d4u_1vf99yfm1-7etr3tp_47ues2_Xurf_71__3z3_9pxP78k89r7335EQ_v-_v-b7BCPN_Y3v-8K96lPK; _fbp=fb.1.1595435751972.1313492171; _bcvm_vrid_600325845512194259=601168376522962544TEE7CE46D2F6DAC40711E871F4B9C6619452464B550B05ACB59B6C9EBBB9950BC8D08C8E7E35682E623737206988550A8E55E5D2BC274FA8D91C1869D2F5D027C; fbm_197335844169=base_domain=.bodybuilding.com; v1guid=bc90daca53dcce435d142cdc62050f93660d4003; lgn=1; _pin_unauth=dWlkPU9UaG1aakZoWldNdE1HSXpZaTAwTUdZeExXRTRZalF0TkRZeU16ZGxPRGN5TVRsag; DYN_USER_ID=4257090281; DYN_USER_CONFIRM=82984f69f88d1c565b78ee4fbaba5a6c; _gac_UA-55035870-1=1.1595513664.EAIaIQobChMI4PCG6sbj6gIVyfhRCh2PCw_KEAAYASAAEgL4A_D_BwE; _gcl_aw=GCL.1595513665.EAIaIQobChMI4PCG6sbj6gIVyfhRCh2PCw_KEAAYASAAEgL4A_D_BwE; _gac_UA-55035870-1=1.1595513664.EAIaIQobChMI4PCG6sbj6gIVyfhRCh2PCw_KEAAYASAAEgL4A_D_BwE; og_session_id=75f637dc7eaf11e6b517bc764e106cf4.348310.1595578968; cnx_start=1595578970696; cnx_sid=189894219692016765; cnx_views=1; cnx_pg=1595578970696; cnx_orid=1595578971411353085_pos; c=be0acf59910343928a223ebeabfe5e73; __insp_slim=1596350235332; __insp_wid=2024354313; __insp_nv=true; __insp_targlpt=Qm9keWJ1aWxkaW5nLmNvbSAtIEh1Z2UgT25saW5lIFN1cHBsZW1lbnQgU3RvcmUgJiBGaXRuZXNzIENvbW11bml0eSE%3D; __insp_targlpu=aHR0cHM6Ly93d3cuYm9keWJ1aWxkaW5nLmNvbS9lbi1JTC9pbmRleA%3D%3D; __insp_norec_sess=true; lc=IL|en; _ga=GA1.2.472499012.1596606494; _gid=GA1.2.1552245095.1596606494; _awl=2.1596606497.0.4-a8a08013-14a98f1d5d214fbc924c0b0ca4f27f50-6763652d6575726f70652d7765737431-5f2a4821-1; _gid=GA1.3.1552245095.1596606494; _uetsid=b58ac51a066a1ded192f3250ff961e72; _uetvid=5d9fc4304151874e6811e4fe2fd4fdd1; _awl=3.1596609397.0.4-24fb3a49-14a98f1d5d214fbc924c0b0ca4f27f50-6763652d6575726f70652d7765737431-5f2a5375-1; RES_SESSIONID=473455300670857; _bcvm_vid_600325845512194259=601180113305105511TAE885C7E0ECDC4499EFEE9C1897D742A2AB9BCFB5E59DC9C093AB094B96A179872CA33680C81BD0B9C329E0745D7B1F8F6D7295AEFCA00C59584297CA5880962; _gat_UA-55035870-1=1; bc_pv_end=601180113426370520T84F2B72074C95EE8843AEFD68784B1622368D13DB681CC54405D3291BCC4579D7102462249B1AA3895D9A1FBEE962023E9E05B9C0178910E2400D15E3AD129A0")
//                .build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

}
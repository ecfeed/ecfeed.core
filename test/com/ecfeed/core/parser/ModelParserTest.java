package com.ecfeed.core.parser;

import com.ecfeed.core.model.*;
import com.ecfeed.core.parser.model.ModelData;
import com.ecfeed.core.parser.model.ModelDataFactory;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

public class ModelParserTest {

    @Test
    public void parseModelCSVTest() {

        ModelData model = ModelDataFactory.create("Country,Name,Address,Product,Color,Size,Quantity,Payment,Delivery,Phone,Email\n" +
                        "Poland,John Doe,Timmersloher Landstraße 300. D-12129 Kuhdorf,hoodie,black,S,53,VISA,standard,+48123456789,Z761@c92.com\n" +
                        "Luxembourg,Ola Nordmann,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,red,XS,40,cash on delivery,PostNL,+47123456789,pV5@gt2.net\n" +
                        "Netherlands,Anna Kowalska,Aleja Targowa 129. 03-728 Warszawa,hoodie,white,L,9,MASTERCARD,express,+48123456789,1g1m5@35.net\n" +
                        "Belgium,Ola Nordmann,Aleja Targowa 129. 03-728 Warszawa,t-shirt,blue,M,4,bank transfer,standard,+00123456789,2ox9@u8W.com\n" +
                        "Norway,Max Mustermann,Timmersloher Landstraße 300. D-12129 Kuhdorf,hoodie,black,M,1,cash on delivery,express,+47123456789,AZ3@mr.net\n" +
                        "Luxembourg,Max Mustermann,Timmersloher Landstraße 300. D-12129 Kuhdorf,t-shirt,green,XL,7,MASTERCARD,PostNL,+00123456789,T59n@13Z.com\n" +
                        "other,Anna Kowalska,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,red,L,6,VISA,standard,+00123456789,S93lu@a6y.com\n" +
                        "Norway,John Doe,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,white,XL,14,bank transfer,express,+47123456789,W1Yi@i3.com\n" +
                        "Belgium,John Doe,Aleja Targowa 129. 03-728 Warszawa,hoodie,black,XS,7,bank transfer,PostNL,+00123456789,on1CE@w5y.net\n" +
                        "Netherlands,Ola Nordmann,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,green,S,3,cash on delivery,PostNL,+48123456789,jm2@sBK.com\n" +
                        "Netherlands,Max Mustermann,Aleja Targowa 129. 03-728 Warszawa,t-shirt,blue,XL,70,VISA,standard,+48123456789,w7T@lQK.net\n" +
                        "Poland,Max Mustermann,Aleja Targowa 129. 03-728 Warszawa,t-shirt,red,S,6,bank transfer,express,+48123456789,w9EWD@7Gf.net\n" +
                        "other,Ola Nordmann,Timmersloher Landstraße 300. D-12129 Kuhdorf,hoodie,white,XS,4,MASTERCARD,standard,+00123456789,iw6@BxF.com\n" +
                        "Poland,Anna Kowalska,Aleja Targowa 129. 03-728 Warszawa,t-shirt,green,XL,3,cash on delivery,standard,+48123456789,Qwb@jiC.net\n" +
                        "Luxembourg,Anna Kowalska,Timmersloher Landstraße 300. D-12129 Kuhdorf,t-shirt,blue,L,70,bank transfer,PostNL,+47123456789,3YIHG@V6.net\n" +
                        "Belgium,Max Mustermann,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,green,XS,17,VISA,express,+48123456789,VR6a9@nS8.com\n" +
                        "Luxembourg,John Doe,Aleja Targowa 129. 03-728 Warszawa,hoodie,white,M,1,VISA,PostNL,+47123456789,61Djc@1CY.net\n" +
                        "Norway,Ola Nordmann,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,black,L,9,MASTERCARD,express,+47123456789,KY6@j2T.net\n" +
                        "other,John Doe,Aleja Targowa 129. 03-728 Warszawa,t-shirt,green,M,50,bank transfer,standard,+00123456789,Xbqw@pGO.net\n" +
                        "Poland,John Doe,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,blue,M,6,MASTERCARD,express,+48123456789,dav2o@1z.net\n" +
                        "Belgium,Anna Kowalska,Timmersloher Landstraße 300. D-12129 Kuhdorf,t-shirt,red,S,2,MASTERCARD,standard,+47123456789,4XoBG@7EG.net\n" +
                        "Netherlands,John Doe,Timmersloher Landstraße 300. D-12129 Kuhdorf,t-shirt,blue,XS,8,cash on delivery,express,+00123456789,125Ug@aTa.net\n" +
                        "Norway,Anna Kowalska,Aleja Targowa 129. 03-728 Warszawa,t-shirt,blue,S,4,VISA,standard,+47123456789,cOp8q@92.com\n" +
                        "Belgium,Max Mustermann,Tollbodgata 138. 0484 Langtvekkistan,hoodie,white,L,5,cash on delivery,express,+00123456789,2mp@zB.com\n" +
                        "other,Ola Nordmann,Tollbodgata 138. 0484 Langtvekkistan,hoodie,black,XL,40,VISA,standard,+00123456789,788r6@4Hp.com\n" +
                        "Luxembourg,Anna Kowalska,Aleja Targowa 129. 03-728 Warszawa,t-shirt,black,S,5,VISA,express,+00123456789,1H7b@Eu.net\n" +
                        "Netherlands,Anna Kowalska,Timmersloher Landstraße 300. D-12129 Kuhdorf,t-shirt,red,M,85,bank transfer,PostNL,+47123456789,xwaY@n3I.com\n" +
                        "Poland,Ola Nordmann,Aleja Targowa 129. 03-728 Warszawa,hoodie,white,L,85,bank transfer,express,+48123456789,8jUjB@aYg.com\n" +
                        "Norway,John Doe,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,green,L,2,VISA,express,+47123456789,bVR7@4nH.com\n" +
                        "other,Max Mustermann,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,blue,S,77,bank transfer,standard,+00123456789,2LxgB@v2.net\n" +
                        "Luxembourg,Anna Kowalska,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,green,XS,89,MASTERCARD,standard,+48123456789,o28H7@YbJ.com\n" +
                        "Belgium,John Doe,Aleja Targowa 129. 03-728 Warszawa,t-shirt,red,XL,9,MASTERCARD,PostNL,+00123456789,x3kT2@75T.net\n" +
                        "Poland,Max Mustermann,Tollbodgata 138. 0484 Langtvekkistan,hoodie,black,XS,6,bank transfer,standard,+48123456789,f16M@mrQ.com\n" +
                        "Norway,John Doe,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,red,XS,2,MASTERCARD,standard,+47123456789,87WeS@LxU.com\n" +
                        "Luxembourg,Ola Nordmann,Aleja Targowa 129. 03-728 Warszawa,t-shirt,white,S,8,VISA,express,+48123456789,4RKE3@gD3.net\n" +
                        "Netherlands,Max Mustermann,Timmersloher Landstraße 300. D-12129 Kuhdorf,t-shirt,black,L,3,bank transfer,PostNL,+00123456789,9EC@1Wn.net\n",
                ModelDataFactory.Type.CSV);

        List<String> data = model.getRaw();
        List<String> header = model.getHeader();
        List<Set<String>> body = model.getParameters();

        Assert.assertEquals(37, data.size());
        Assert.assertTrue(data.stream().filter(e -> e.equals("Country,Name,Address,Product,Color,Size,Quantity,Payment,Delivery,Phone,Email")).count() > 0);

        Assert.assertEquals(11, header.size());

        Assert.assertEquals(11, body.size());
        Assert.assertEquals(6, body.get(0).size());
        Assert.assertEquals(4, body.get(1).size());
        Assert.assertEquals(3, body.get(2).size());
        Assert.assertEquals(2, body.get(3).size());
        Assert.assertEquals(5, body.get(4).size());
        Assert.assertEquals(5, body.get(5).size());
        Assert.assertEquals(18, body.get(6).size());
        Assert.assertEquals(4, body.get(7).size());
        Assert.assertEquals(3, body.get(8).size());
        Assert.assertEquals(3, body.get(9).size());
        Assert.assertEquals(36, body.get(10).size());

        RootNode parentRoot = new RootNode("test", new ModelChangeRegistrator(), 1);
        List<BasicParameterNode> parentRootData = model.parse(parentRoot);

        Assert.assertEquals(11, parentRootData.size());
        Assert.assertTrue(parentRootData.get(0) instanceof BasicParameterNode);

        ClassNode parentClass = new ClassNode("test", parentRoot.getModelChangeRegistrator());
        List<BasicParameterNode> parentClassData = model.parse(parentClass);

        Assert.assertEquals(11, parentClassData.size());
        Assert.assertTrue(parentClassData.get(0) instanceof BasicParameterNode);

        MethodNode parentMethod = new MethodNode("test", parentRoot.getModelChangeRegistrator());
        List<BasicParameterNode> parentMethodData = model.parse(parentMethod);

        Assert.assertEquals(11, parentMethodData.size());
        Assert.assertTrue(parentMethodData.get(0) instanceof BasicParameterNode);
    }

    @Test
    void parseModelCSVErrorTest() {

        Assertions.assertThrows(Exception.class, () -> {
            ModelDataFactory.create("one, two, three\n" +
                            "one, two",
                    ModelDataFactory.Type.CSV);
        });
    }

    @Test
    void parseModelCSVOverflowTest() {

        ModelData model = ModelDataFactory.create("Country,Name,Address,Product,Color,Size,Quantity,Payment,Delivery,Phone,Email\n" +
                        "Poland,John Doe,Timmersloher Landstraße 300. D-12129 Kuhdorf,hoodie,black,S,53,VISA,standard,+48123456789,Z761@c92.com\n" +
                        "Luxembourg,Ola Nordmann,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,red,XS,40,cash on delivery,PostNL,+47123456789,pV5@gt2.net\n" +
                        "Netherlands,Anna Kowalska,Aleja Targowa 129. 03-728 Warszawa,hoodie,white,L,9,MASTERCARD,express,+48123456789,1g1m5@35.net\n" +
                        "Belgium,Ola Nordmann,Aleja Targowa 129. 03-728 Warszawa,t-shirt,blue,M,4,bank transfer,standard,+00123456789,2ox9@u8W.com\n" +
                        "Norway,Max Mustermann,Timmersloher Landstraße 300. D-12129 Kuhdorf,hoodie,black,M,1,cash on delivery,express,+47123456789,AZ3@mr.net\n" +
                        "Luxembourg,Max Mustermann,Timmersloher Landstraße 300. D-12129 Kuhdorf,t-shirt,green,XL,7,MASTERCARD,PostNL,+00123456789,T59n@13Z.com\n" +
                        "other,Anna Kowalska,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,red,L,6,VISA,standard,+00123456789,S93lu@a6y.com\n" +
                        "Norway,John Doe,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,white,XL,14,bank transfer,express,+47123456789,W1Yi@i3.com\n" +
                        "Belgium,John Doe,Aleja Targowa 129. 03-728 Warszawa,hoodie,black,XS,7,bank transfer,PostNL,+00123456789,on1CE@w5y.net\n" +
                        "Netherlands,Ola Nordmann,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,green,S,3,cash on delivery,PostNL,+48123456789,jm2@sBK.com\n" +
                        "Netherlands,Max Mustermann,Aleja Targowa 129. 03-728 Warszawa,t-shirt,blue,XL,70,VISA,standard,+48123456789,w7T@lQK.net\n" +
                        "Poland,Max Mustermann,Aleja Targowa 129. 03-728 Warszawa,t-shirt,red,S,6,bank transfer,express,+48123456789,w9EWD@7Gf.net\n" +
                        "other,Ola Nordmann,Timmersloher Landstraße 300. D-12129 Kuhdorf,hoodie,white,XS,4,MASTERCARD,standard,+00123456789,iw6@BxF.com\n" +
                        "Poland,Anna Kowalska,Aleja Targowa 129. 03-728 Warszawa,t-shirt,green,XL,3,cash on delivery,standard,+48123456789,Qwb@jiC.net\n" +
                        "Luxembourg,Anna Kowalska,Timmersloher Landstraße 300. D-12129 Kuhdorf,t-shirt,blue,L,70,bank transfer,PostNL,+47123456789,3YIHG@V6.net\n" +
                        "Belgium,Max Mustermann,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,green,XS,17,VISA,express,+48123456789,VR6a9@nS8.com\n" +
                        "Luxembourg,John Doe,Aleja Targowa 129. 03-728 Warszawa,hoodie,white,M,1,VISA,PostNL,+47123456789,61Djc@1CY.net\n" +
                        "Norway,Ola Nordmann,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,black,L,9,MASTERCARD,express,+47123456789,KY6@j2T.net\n" +
                        "other,John Doe,Aleja Targowa 129. 03-728 Warszawa,t-shirt,green,M,50,bank transfer,standard,+00123456789,Xbqw@pGO.net\n" +
                        "Poland,John Doe,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,blue,M,6,MASTERCARD,express,+48123456789,dav2o@1z.net\n" +
                        "Belgium,Anna Kowalska,Timmersloher Landstraße 300. D-12129 Kuhdorf,t-shirt,red,S,2,MASTERCARD,standard,+47123456789,4XoBG@7EG.net\n" +
                        "Netherlands,John Doe,Timmersloher Landstraße 300. D-12129 Kuhdorf,t-shirt,blue,XS,8,cash on delivery,express,+00123456789,125Ug@aTa.net\n" +
                        "Norway,Anna Kowalska,Aleja Targowa 129. 03-728 Warszawa,t-shirt,blue,S,4,VISA,standard,+47123456789,cOp8q@92.com\n" +
                        "Belgium,Max Mustermann,Tollbodgata 138. 0484 Langtvekkistan,hoodie,white,L,5,cash on delivery,express,+00123456789,2mp@zB.com\n" +
                        "other,Ola Nordmann,Tollbodgata 138. 0484 Langtvekkistan,hoodie,black,XL,40,VISA,standard,+00123456789,788r6@4Hp.com\n" +
                        "Luxembourg,Anna Kowalska,Aleja Targowa 129. 03-728 Warszawa,t-shirt,black,S,5,VISA,express,+00123456789,1H7b@Eu.net\n" +
                        "Netherlands,Anna Kowalska,Timmersloher Landstraße 300. D-12129 Kuhdorf,t-shirt,red,M,85,bank transfer,PostNL,+47123456789,xwaY@n3I.com\n" +
                        "Poland,Ola Nordmann,Aleja Targowa 129. 03-728 Warszawa,hoodie,white,L,85,bank transfer,express,+48123456789,8jUjB@aYg.com\n" +
                        "Norway,John Doe,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,green,L,2,VISA,express,+47123456789,bVR7@4nH.com\n" +
                        "other,Max Mustermann,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,blue,S,77,bank transfer,standard,+00123456789,2LxgB@v2.net\n" +
                        "Luxembourg,Anna Kowalska,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,green,XS,89,MASTERCARD,standard,+48123456789,o28H7@YbJ.com\n" +
                        "Belgium,John Doe,Aleja Targowa 129. 03-728 Warszawa,t-shirt,red,XL,9,MASTERCARD,PostNL,+00123456789,x3kT2@75T.net\n" +
                        "Poland,Max Mustermann,Tollbodgata 138. 0484 Langtvekkistan,hoodie,black,XS,6,bank transfer,standard,+48123456789,f16M@mrQ.com\n" +
                        "Norway,John Doe,Tollbodgata 138. 0484 Langtvekkistan,t-shirt,red,XS,2,MASTERCARD,standard,+47123456789,87WeS@LxU.com\n" +
                        "Luxembourg,Ola Nordmann,Aleja Targowa 129. 03-728 Warszawa,t-shirt,white,S,8,VISA,express,+48123456789,4RKE3@gD3.net\n" +
                        "Netherlands,Max Mustermann,Timmersloher Landstraße 300. D-12129 Kuhdorf,t-shirt,black,L,3,bank transfer,PostNL,+00123456789,9EC@1Wn.net\n",
                ModelDataFactory.Type.CSV);

        Assertions.assertFalse(model.getWarning().isPresent());

        model.setLimit(5);

        Assertions.assertEquals(3, model.getHeaderAffected().size());
        Assertions.assertTrue(model.getWarning().isPresent());
    }
}

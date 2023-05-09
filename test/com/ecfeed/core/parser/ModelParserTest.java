package com.ecfeed.core.parser;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.ModelChangeRegistrator;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.parser.model.ModelData;
import com.ecfeed.core.parser.model.ModelDataFactory;

public class ModelParserTest {

    @Test
    void parseModelCSVTest() {

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

        Assertions.assertEquals(37, data.size());
        Assertions.assertTrue(data.stream().anyMatch(e -> e.equals("Country,Name,Address,Product,Color,Size,Quantity,Payment,Delivery,Phone,Email")));

        Assertions.assertEquals(11, header.size());

        Assertions.assertEquals(11, body.size());
        Assertions.assertEquals(6, body.get(0).size());
        Assertions.assertEquals(4, body.get(1).size());
        Assertions.assertEquals(3, body.get(2).size());
        Assertions.assertEquals(2, body.get(3).size());
        Assertions.assertEquals(5, body.get(4).size());
        Assertions.assertEquals(5, body.get(5).size());
        Assertions.assertEquals(18, body.get(6).size());
        Assertions.assertEquals(4, body.get(7).size());
        Assertions.assertEquals(3, body.get(8).size());
        Assertions.assertEquals(3, body.get(9).size());
        Assertions.assertEquals(36, body.get(10).size());

        RootNode parentRoot = new RootNode("test", new ModelChangeRegistrator(), 1);
        List<BasicParameterNode> parentRootData = model.parse(parentRoot);

        Assertions.assertEquals(11, parentRootData.size());
        Assertions.assertNotNull(parentRootData.get(0));

        ClassNode parentClass = new ClassNode("test", parentRoot.getModelChangeRegistrator());
        List<BasicParameterNode> parentClassData = model.parse(parentClass);

        Assertions.assertEquals(11, parentClassData.size());
        Assertions.assertNotNull(parentClassData.get(0));

        MethodNode parentMethod = new MethodNode("test", parentRoot.getModelChangeRegistrator());
        List<BasicParameterNode> parentMethodData = model.parse(parentMethod);

        Assertions.assertEquals(11, parentMethodData.size());
        Assertions.assertNotNull(parentMethodData.get(0));
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

    @Test
    public void parseModelCSVSeparatorTest() {

        ModelData model = ModelDataFactory.create("Country;Name;Address;Product;Color;Size;Quantity;Payment;Delivery;Phone;Email\n" +
                        "Poland;John Doe;Timmersloher Landstraße 300. D-12129 Kuhdorf;hoodie;black;S;53;VISA;standard;+48123456789;Z761@c92.com\n" +
                        "Luxembourg;Ola Nordmann;Tollbodgata 138. 0484 Langtvekkistan;t-shirt;red;XS;40;cash on delivery;PostNL;+47123456789;pV5@gt2.net",
                ModelDataFactory.Type.CSV);

        List<String> data = model.getRaw();
        List<String> header = model.getHeader();
        List<Set<String>> body = model.getParameters();

        Assertions.assertEquals(3, data.size());
        Assertions.assertTrue(data.stream().anyMatch(e -> e.equals("Country;Name;Address;Product;Color;Size;Quantity;Payment;Delivery;Phone;Email")));

        Assertions.assertEquals(11, header.size());

        Assertions.assertEquals(11, body.size());
        Assertions.assertEquals(2, body.get(0).size());
        Assertions.assertEquals(2, body.get(1).size());
        Assertions.assertEquals(2, body.get(2).size());
        Assertions.assertEquals(2, body.get(3).size());
        Assertions.assertEquals(2, body.get(4).size());
        Assertions.assertEquals(2, body.get(5).size());
        Assertions.assertEquals(2, body.get(6).size());
        Assertions.assertEquals(2, body.get(7).size());
        Assertions.assertEquals(2, body.get(8).size());
        Assertions.assertEquals(2, body.get(9).size());
        Assertions.assertEquals(2, body.get(10).size());

        RootNode parentRoot = new RootNode("test", new ModelChangeRegistrator(), 1);
        List<BasicParameterNode> parentRootData = model.parse(parentRoot);

        Assertions.assertEquals(11, parentRootData.size());
        Assertions.assertNotNull(parentRootData.get(0));

        ClassNode parentClass = new ClassNode("test", parentRoot.getModelChangeRegistrator());
        List<BasicParameterNode> parentClassData = model.parse(parentClass);

        Assertions.assertEquals(11, parentClassData.size());
        Assertions.assertNotNull(parentClassData.get(0));

        MethodNode parentMethod = new MethodNode("test", parentRoot.getModelChangeRegistrator());
        List<BasicParameterNode> parentMethodData = model.parse(parentMethod);

        Assertions.assertEquals(11, parentMethodData.size());
        Assertions.assertNotNull(parentMethodData.get(0));
    }

    @Test
    public void parseModelCSVSeparatorMixFailureTest() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> ModelDataFactory.create("Country;Name;Address;Product;Color;Size;Quantity;Payment;Delivery;Phone;Email\n" +
                        "Poland;John Doe,Timmersloher Landstraße 300. D-12129 Kuhdorf,hoodie;black;S;53;VISA;standard;+48123456789;Z761@c92.com\n" +
                        "Luxembourg,Ola Nordmann;Tollbodgata 138. 0484 Langtvekkistan;t-shirt;red;XS;40;cash on delivery;PostNL;+47123456789;pV5@gt2.net",
                ModelDataFactory.Type.CSV));
    }

    @Test
    public void parseModelCSVTrimTest() {

        ModelData model = ModelDataFactory.create(" Country ;  Name ; Address;  Product;Color  ; Size ;    Quantity;Payment    ; Delivery; Phone; Email  \n" +
                        "Poland;John Doe;Timmersloher Landstraße 300. D-12129 Kuhdorf;hoodie;black;S;53;VISA;standard;+48123456789;Z761@c92.com\n" +
                        "Luxembourg;Ola Nordmann;Tollbodgata 138. 0484 Langtvekkistan;t-shirt;red;XS;40;cash on delivery;PostNL;+47123456789;pV5@gt2.net",
                ModelDataFactory.Type.CSV);

        List<String> header = model.getHeader();

        Assertions.assertEquals(11, header.size());

        for (String column : header) {
            if (column.startsWith(" ") || column.endsWith(" ")) {
                Assertions.fail();
            }
        }
    }

    @Test
    public void parseModelCSVParseColumnNameTrimTest() {

        ModelData model = ModelDataFactory.create(" C o u ntry ;  Nam  e ; Ad dress;  Pr o duct;Co lor  ; S ize ;    Quan tity;Pa yment    ; Deliv ery; P hone; Emai l  ",
                ModelDataFactory.Type.CSV);

        List<String> header = model.getHeader();

        Assertions.assertEquals(11, header.size());

        for (String column : header) {
            if (column.startsWith(" ") || column.endsWith(" ")) {
                Assertions.fail();
            }
        }

        RootNode parentRoot = new RootNode("test", new ModelChangeRegistrator(), 1);
        List<BasicParameterNode> parentRootData = model.parse(parentRoot);

        Assertions.assertEquals(11, parentRootData.size());
    }

    @Test
    public void parseModelCSVParseColumnNameNonAlphanumericalTest() {

        ModelData model = ModelDataFactory.create("År;Orgnr;Gjeldende navn;Antall (obligatorisk manntall);Omsetningsoppg Grunnl Sum Samlet Omsetning Innenfor;Grunnl  Oms Utenfor Mva;4_15 Omsetningsoppg Grunnl Inng Mva (Alle satser)#1;7_02 Rene Fasts Grunnl Sum Avgpl Omsetning;7_15 Rene Fasts Grunnl Inng Mva (Alle Satser);Antall Terminer Postert;filnavn_O_ORGNR_Mva",
                ModelDataFactory.Type.CSV);

        List<String> header = model.getHeader();

        Assertions.assertEquals(11, header.size());

        RootNode parentRoot = new RootNode("test", new ModelChangeRegistrator(), 1);
        List<BasicParameterNode> parentRootData = model.parse(parentRoot);

        Assertions.assertEquals(11, parentRootData.size());
    }

    @Test
    public void parseModelCSVEmptyFieldTest() {

        ModelData model = ModelDataFactory.create("År;Orgnr;Gjeldende navn;Antall (obligatorisk manntall);Omsetningsoppg Grunnl Sum Samlet Omsetning Innenfor;Grunnl  Oms Utenfor Mva;4_15 Omsetningsoppg Grunnl Inng Mva (Alle satser)#1;7_02 Rene Fasts Grunnl Sum Avgpl Omsetning;7_15 Rene Fasts Grunnl Inng Mva (Alle Satser);Antall Terminer Postert;filnavn_O_ORGNR_Mva\n" +
                "2022;812917714;aoga;1;;;;0;0;;\n" +
                "2022;927227592;bogb;1;0;0;0;0;0;4;\n" +
                "2022;311166980;cogc;1;210189;0;0;207739;0;6;\n" +
                "2022;913216032;dogd;1;798053;0;170900;0;0;1;\n" +
                "2022;832645257;eoge;1;821000;0;19200;821000;19200;6;\n" +
                "2022;314802977;fogf;1;;;;0;0;;\n" +
                "2022;851627162;gogg;1;321880;0;219021;0;0;1;\n" +
                "2022;804499342;hogh;1;;;;0;0;;", ModelDataFactory.Type.CSV);

        List<String> header = model.getHeader();

        Assertions.assertEquals(11, header.size());

        RootNode parentRoot = new RootNode("test", new ModelChangeRegistrator(), 1);
        List<BasicParameterNode> parentRootData = model.parse(parentRoot);

        Assertions.assertEquals(11, parentRootData.size());
    }
}

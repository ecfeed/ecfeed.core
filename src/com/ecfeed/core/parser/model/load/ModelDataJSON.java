package com.ecfeed.core.parser.model.load;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.IModelChangeRegistrator;

import java.nio.file.Path;
import java.util.List;

public class ModelDataJSON extends ModelDataAbstract {

    public static ModelData getModelData(Path path) {

        return new ModelDataJSON(path);
    }

    public static ModelData getModelData(String data) {

        return new ModelDataJSON(data);
    }

    private ModelDataJSON(Path path) {

        create(path);
    }

    private ModelDataJSON(String data) {

        create(data);
    }

    @Override
    protected void createInternal() {

    }

    @Override
    public List<AbstractParameterNode> parse(IModelChangeRegistrator registrator) {
        return null;
    }


}

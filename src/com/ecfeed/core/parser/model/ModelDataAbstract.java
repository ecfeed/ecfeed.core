package com.ecfeed.core.parser.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ecfeed.core.model.AbstractParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.ParametersParentNode;
import com.ecfeed.core.model.RootNode;

abstract class ModelDataAbstract implements ModelData {

    protected List<String> data;
    protected List<String> header;
    protected List<Set<String>> body;

    protected final void create(Path path) {

        validateFile(path);
        initializeDataFile(path);

        initializeHeader();
        initializeBody();
        process();
    }

    protected final void create(String data) {

        validateText(data);
        initializeDataText(data);

        initializeHeader();
        initializeBody();
        process();
    }

    private final void validateFile(Path path) {

        if (Files.notExists(path)) {
            throw new IllegalArgumentException("The file '" + path.toAbsolutePath() + "' does not exist.");
        }

        if (Files.isDirectory(path)) {
            throw new IllegalArgumentException("The file '" + path.toAbsolutePath() + "' is a directory.");
        }

        if (Files.isSymbolicLink(path)) {
            throw new IllegalArgumentException("The file '" + path.toAbsolutePath() + "' is a symbolic link.");
        }

        if (!Files.isReadable(path)) {
            throw new IllegalArgumentException("The file '" + path.toAbsolutePath() + "' is not readable.");
        }
    }

    private final void initializeDataFile(Path path) {

        try {
            this.data = Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException("An I/O error occurred while opening the file: '" + path.toAbsolutePath() + "'.", e);
        }
    }

    private final void validateText(String data) {
    }

    private final void initializeDataText(String data) {

    	this.data = Arrays.asList(data.split("\n"));
    }

	protected final void initializeBody() {
        this.body = new ArrayList<>();

        for (int i = 0 ; i < this.header.size() ; i++) {
        	this.body.add(new HashSet<String>());
        }
    }

    protected abstract void initializeHeader();

    protected abstract void process();

    @Override
    public List<AbstractParameterNode> parse(ParametersParentNode node) {
    	List<AbstractParameterNode> list = new ArrayList<>();
    	
        for (int i = 0 ; i < this.header.size() ; i++) {
        	List<ChoiceNode> choices = new ArrayList<>();
        	DataType type = DataTypeFactory.create();

            int j = 0;
            for (String choice : this.body.get(i)) {
            	type.feed(choice);
                choices.add(new ChoiceNode("choice" + (j++), choice, null));
            }
            
            AbstractParameterNode parameter = null;
            
            if (node instanceof MethodNode) {
            	parameter = new MethodParameterNode(this.header.get(i), type.determine(), "", false, node.getModelChangeRegistrator());
            } else if (node instanceof ClassNode) {
            	parameter = new GlobalParameterNode(this.header.get(i), type.determine(), node.getModelChangeRegistrator());
            } else if (node instanceof RootNode) {
            	parameter = new GlobalParameterNode(this.header.get(i), type.determine(), node.getModelChangeRegistrator());
            } else {
            	throw new IllegalArgumentException("The node type is not supported.");
            }
            
            choices.forEach(parameter::addChoice);

            list.add(parameter);
        }
        
        return list;
    }
}

package com.ecfeed.core.parser.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import com.ecfeed.core.model.BasicParameterNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.IParametersParentNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.RootNode;

abstract class ModelDataAbstract implements ModelData {

    protected int limit = 100;
    protected List<String> raw;
    protected List<String> header;
    protected List<String> headerAffected;
    protected List<Set<String>> body;

    @Override
    public List<String> getRaw() {

        return raw;
    }

    @Override
    public List<String> getHeader() {

        return header;
    }

    @Override
    public List<String> getHeaderAffected() {

        return headerAffected;
    }

    @Override
    public List<Set<String>> getParameters() {

        return body;
    }

    @Override
    public int getLimit() {

        return this.limit;
    }

    @Override
    public void setLimit(int limit) {

    	this.limit = limit;

        validateSize();
    }
    
    protected final void create(Path path) {
        this.headerAffected = new ArrayList<>();

        validateFile(path);

        initializeDataFile(path);

        updateProperties();

        initializeHeader();
        initializeBody();

        process();

        validateSize();
    }

    protected final void create(String data) {
        this.headerAffected = new ArrayList<>();

        validateText(data);

        initializeDataText(data);

        updateProperties();

        initializeHeader();
        initializeBody();

        process();

        validateSize();
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
            this.raw = Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException("An I/O error occurred while opening the file: '" + path.toAbsolutePath() + "'.", e);
        }
    }

    private final void validateText(String data) {
    }

    private final void initializeDataText(String data) {

    	this.raw = Arrays.asList(data.split("\n"));
    }

	private final void initializeBody() {
        this.body = new ArrayList<>();

        for (int i = 0 ; i < this.header.size() ; i++) {
        	this.body.add(new HashSet<>());
        }
    }

    protected abstract void updateProperties();

    protected abstract void initializeHeader();

    protected abstract void process();

    private final void validateSize() {
    	this.headerAffected.clear();

    	IntStream.range(0, this.body.size()).forEach(i -> {
        	if (this.body.get(i).size() > this.limit) {
        		this.headerAffected.add(this.header.get(i));
        	}
        });
    }
    
    @Override
    public List<BasicParameterNode> parse(IParametersParentNode node) {
    	List<BasicParameterNode> list = new ArrayList<>();
    	
        for (int i = 0 ; i < this.header.size() ; i++) {
        	List<ChoiceNode> choices = new ArrayList<>();
        	DataType type = DataTypeFactory.create();

            int j = 0;
            for (String choice : this.body.get(i)) {

                if (j >= this.limit) {
                    break;
                }

            	type.feed(choice);
                choices.add(new ChoiceNode("choice" + (j++), choice, null));
            }
            
            BasicParameterNode parameter;
            
            if (node instanceof MethodNode) {
            	parameter = new BasicParameterNode(this.header.get(i), type.determine(), "", false, node.getModelChangeRegistrator());
            } else if (node instanceof ClassNode) {
            	parameter = new BasicParameterNode(this.header.get(i), type.determine(), "0", false, node.getModelChangeRegistrator());
            } else if (node instanceof RootNode) {
            	parameter = new BasicParameterNode(this.header.get(i), type.determine(), "0", false, node.getModelChangeRegistrator());
            } else {
            	throw new IllegalArgumentException("The node type is not supported.");
            }
            
            choices.forEach(parameter::addChoice);

            list.add(parameter);
        }
        
        return list;
    }

    @Override
    public Optional<String> getWarning() {
    	
    	if (this.headerAffected.size() > 0) {
			return Optional.of("The hard limit for the number of choices is " + this.limit +".\n"
					+ "Affected parameters: " + String.join(", ", this.headerAffected) + ".");
		}
    	
    	return Optional.empty();
    }
}

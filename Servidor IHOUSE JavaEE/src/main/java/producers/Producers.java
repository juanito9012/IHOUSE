package producers;

import org.modelmapper.ModelMapper;

import javax.enterprise.inject.Produces;

public class Producers {
    @Produces
    public ModelMapper modelMapperFactory(){
        return new ModelMapper();
    }

}

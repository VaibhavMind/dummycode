package com.payasia.api.hris.model;

public class Filters
{
    private String field;

    private String value;

    public String getField ()
    {
        return field;
    }

    public void setField (String field)
    {
        this.field = field;
    }

    public String getValue ()
    {
        return value;
    }

    public void setValue (String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [field = "+field+", value = "+value+"]";
    }
}
			
	
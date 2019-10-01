package com.payasia.api.hris.model;

public class SearchParam {

	private MultiSortMeta[] multiSortMeta;

	private GlobalFilter globalFilter;

    private String sortField;
    
    private String sortOrder;

    private int page;

    private Filters[] filters;

    private int rows;

    public MultiSortMeta[] getMultiSortMeta ()
    {
        return multiSortMeta;
    }

    public void setMultiSortMeta (MultiSortMeta[] multiSortMeta)
    {
        this.multiSortMeta = multiSortMeta;
    }

    public GlobalFilter getGlobalFilter ()
    {
        return globalFilter;
    }

    public void setGlobalFilter (GlobalFilter globalFilter)
    {
        this.globalFilter = globalFilter;
    }

    public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getSortOrder ()
    {
        return sortOrder;
    }

    public void setSortOrder (String sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public int getPage ()
    {
        return page;
    }

    public void setPage (int page)
    {
        this.page = page;
    }

    public Filters[] getFilters ()
    {
        return filters;
    }

    public void setFilters (Filters[] filters)
    {
        this.filters = filters;
    }

    public int getRows ()
    {
        return rows;
    }

    public void setRows (int rows)
    {
        this.rows = rows;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [multiSortMeta = "+multiSortMeta+", globalFilter = "+globalFilter+", sortOrder = "+sortOrder+", page  = "+page+", filters = "+filters+", rows = "+rows+"]";
    }
}

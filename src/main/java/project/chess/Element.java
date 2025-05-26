package hashing;

class Element
{
    private int key;
    private String value;
    private boolean deleted;

    public Element(int key, String value)
    {
        this.key = key;
        this.value = value;
        this.deleted = false;
    }

    public int Key() { return this.key; }

    public String Value() { return this.value; }

    public boolean Deleted() { return this.deleted; }

    public void SetDeleted(boolean status) { this.deleted = status; };
}



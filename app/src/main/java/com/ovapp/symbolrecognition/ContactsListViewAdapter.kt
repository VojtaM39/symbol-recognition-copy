package com.ovapp.symbolrecognition

/**
class ContactsListViewAdapter(private val activity: Activity, contactsList: List<Contact>) : BaseAdapter()
{
    private var contactsList: ArrayList<Contact>

    init {
        this.contactsList = contactsList as ArrayList
    }

    override fun getCount(): Int {
        return contactsList.size
    }

    override fun getItem(i: Int): Any {
        return i
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }
/**
    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup): View {
        var vi: View = convertView as View
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        vi = inflater.inflate(R.layout.activity_listview_items, null)
        val name = vi.findViewById<TextView>(R.id.tvName)
        val phoneNumber = vi.findViewById<TextView>(R.id.tvPhoneNumber)
        name.text = contactsList[i].name
        phoneNumber.text = contactsList[i].phoneNumber
        return vi
    }
    */
}
        */
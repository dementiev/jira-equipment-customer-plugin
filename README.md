jira-equipment-customer-plugin
==============================

Problem description:
The client wants to do on JIRA service system equipment (printers, scanners, etc.).As you know, in the JIRA is problematic to make such references (drop-down lists do not take into account). This means that when registering a service request, a piece of equipment you can not choose what is this type of equipment that the manufacturer, and what his serial number. Ie there is a problem using any external references. 
  
Progress:
To do in an external database (MySQL) a couple of tables. Next, make a number of customfields, through which I can handle and pulling the data from this database. The main thing to do - dependence of the fields through autocomplete. 
 As a template I used my suggestfield plugin.

![Settings Window](https://raw.github.com/dementiev/jira-equipment-customer-plugin/master/plugin-descr.png)
 
 Logic:
1.	Third and second fields automatically calculate each other (as well as uniquely fill the 1st field).
2.	When filling in the 1st field: create autocomplete listings for 2nd and 3rd fields(eliminated inappropriate under the conditions of 1st field). When filling out 2nd or 3rd fires paragraph 1.

 JIRA version 4.2.1
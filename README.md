
# My contact

This is android application developped with android framwork in kotlin to retrieve contact list from phones and upload them 
into lacal database to display them

## Acknowledgements

 - [android developper](https://developer.android.com/)
 - [stack over flow](https://stackoverflow.com/)
 - [meduim](https://medium.com/)


## Documentation

In this section we going to specify our some project stucture.

Application Architeture:

This Application follow MVVM. 


- Model: divide into to section content_provider and room 

    *content_provider:Contain the logic to retrive data from the device

    *room:Contain The logic to create and manage local database

- ViewModel: Contain one class for all Fragments


- View: Exist 4 Fragments and 1 ListAdpter for the contact list
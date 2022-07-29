The task for the initial job interview was this:

Implement a service with which the user can manage their ads: add, edit, view, delete.
- Model a real estate ad in terms of scala.
- Suggest field validation rules, implement a validator.
- Select ad storage and implement work with it.
- Design and implement API.

You can (and should) use any suitable libraries, protocols and technologies.

Data that should be in the ad (* - required fields with correct values):

- unique identificator*
- price in rubles*
- transaction type* (rent/sale)
- for rent:
    - period* (daily/monthly)
- object type* (apartment/room)
- for the room:
    - square*
    - number of rooms in the apartment
    - kitchen area
- for an apartment:
    - total area*
    - living space
    - kitchen area
- number of rooms
- address*
- seller details:
    - name*
    - telephone*
  
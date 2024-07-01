# Multi-Echelon suppply chain with generational algorithms

**Problem description:**

The chosen problem is multi-echelon supply chain. The problem revolves around 
several levels of storages in supply chains and customers. There are two main 
goals in optimizing this problem: lowest storage expenses and highest product 
availability. Our implementation has a third goal: minimizing delivery expenses.

**Implementation specifics:**

The supply chain is implemented as a graph with 3 levels of warehouses and 1 
level for customers. The 3 types of warehouses in our implementation are: 
Manufacturer, Distributor and Retailer. Storage in each next level of warehouses is 
more expensive than in the previous one. Storages are assumed to have limitless 
capacity. Each storage from one level is connected to every storage of adjacent 
layers, and each customer can receive ordered items from any of the retailers. 

To prevent funneling everything to random retailers we introduced distances, 
which expend money on deliveries either from one warehouse to another, or from 
Retailers to customers. 

Each customer represents some population of people, instead of a specific person. 
Customers place orders every day. Each customer has his own preferences, which 
are shown as two variables: a chance to order a specific item and the amount of 
said item they will order if that chance occurs. Chances should simulate real 
customer preferences and should teach GA to learn patterns in orders from 
particular people.

To keep up with the orders, manufacturers produce a number of items which is 
approximately 1.15 times more than it is expected that the customers will order.

The genotype of the EAs is a list of “tasks”. Each task is a transfer of a certain 
number of items from one storage to another with some chance. The GA cannot 
move an item two levels in a single day (i.e. from Manufacturer to Retailer). 

The implementation is run on a daily basis. The day consists of these stages:
1. The price for storing the items is paid.
2. Manufacturer produces a set number of items.
3. The customers make their orders.
4. If any of the retailers have the ordered items they are forced to sell them, 
earning money for the item, but paying for the delivery.
5. The generational algorithm is activated, moving a set amount of each item 
from one warehouse to another, paying the price for delivery.

The algorithms chosen for the creation of the next generation are Tournament 
selection, Uniform crossover (replaces tasks from one GA and some other GA) and 
Random Scaling mutation (applies a multiplier to Tasks’ chance of happening and 
number of items to be transferred). The next population consists of:
25% of the previous population chosen by selection;
25% from the results of crossover;
50% from the results of mutated crossover GAs.

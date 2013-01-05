Swiss-Army-Plugin
=================

A utilitarian plugin for Bukkit.

Programmed by: Matthew Paul, mpaul0416@gmail.com
Date Created: January 1, 2013
Lase updated: January 5, 2013

Version 1.1.4

This plugin adds several features to a bukkit server.

1. Lumberjack - Uses your axe to automatically chop all the wood attached by either log or leaf.
2. Farmer - Helps automate some aspects of wheat farming
    a. Automatically plants wheat on all continuous soil
    b. Automatically harvests all ripe wheat on continuous soil
    c. Automatically applies bonemeal to all non-ripe wheat on continuous soil

INSTALLATION

1. Just drop it into the plugins folder of the bukkit server.
2. Enjoy lumberjackering while enjoying your assisted-growth wheat

USE

To use the lumberjack, you must be op(Will change if I can figure out how to do permissions!).

To turn on the lumberjackering, type /lumberjack on.
To turn it off, type /lumberjack off.
To see if you're a lumberjack, type /lumberjack status.

When you have lumberjackering turned on, just chop down any log that is part of the tree. It will then cut all the logs and leaves away. Be careful though, it will cut ALL the connected leaves and logs. You can easily deforest an area this way.

To use the farmer, again, you must be op.

To turn on the farmer assistance, type /farmer on.
To turn it off, type /lumberjack off.
To see your farmer status, type /farmer status.

To auto-plant seeds, just click any tilled soil with the seeds in your hand.
To auto-bonemeal wheat, click any unripe wheat with the bonemeal in your hand.
To auto-harvest wheat, click any ripe wheat.


DIRECTIONS TO GO

Add support for permissions
Add support for potatos and carrots
Add support for creative mode
Add shepherd feature

CHANGE-LOG

1.1.4
New Features:
Added support for potatoes and carrots
Added support for creative mode
Bug-Fixes:
Fixed bug with non-farmers being able to recursively harvest
Fixed potential stack overflow with the lumberjack module

Behind the Scenes:
Refactored recursive lumberjack call into iterative call to avoid stack overflow


1.1.3
Moved project onto GitHub
Cleaned up some code to make config files easier

1.1.2 - 1
NOT IMPORTANT ENOUGH TO WRITE. WHO CARES ANYWAY?

---
type: patch
---

Gave each class's constructor a page of its own.

A class used to be documented on the landing page of its own section, which no
navigation ever pointed at. It now has a `constructor` page carrying `index: 0`,
so it sits at the top of the class in the sidebar next to the methods -
`Sprite()` above `addCostume()` - and reads like one of them. The class
description, the example, the constructor syntax and the static fields all moved
there with it. The section's `index` page is left empty; it is the landing page
of the section and nothing more.

The constructor syntax had never appeared on any page: the template asked for
`constructors` while the doclet writes `constructor`, so the block was silently
skipped. It is on the new page, under Syntax, the way a method's syntax is.

A link to a class page, e.g. `/reference/Sprite/`, now arrives at an empty page
rather than at the class documentation, which has moved to
`/reference/Sprite/constructor`. Nothing in the book linked to one.

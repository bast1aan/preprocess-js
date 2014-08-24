PreprocessJS
============

(C) 2014 Bastiaan Welmers

Small utility to preprocess (javascript) source code.

Usage
-----

Usage:

`java PreprocessJS -i main.js -o main.out.js`

or 

`java PreprocessJS < main.js > main.out.js`

It will process the file main.js and output the result to main.out.js.

including
---------

Lines in main.js like:

```js
"include path/to/source.js";
```

will be processed, the line will be replaced by the contents of the
file `path/to/source.js`.

Note that the path/to/source.js will again be processed in the same way.

reading into variables
----------------------

Besides including files, also contents of files can be written
staticly to variables. For example:

```js
 var template;
 "read path/to/template.html into template";
```

will result in the contents of `path/to/template.html` be placed
into the variable `template`.

Considering the contents of `path/to/template.html` is:

```html
 <div>
    <p class="intro">Some text</p>
 </div>
```

The end result for this part will be:

```js
 var template;
 template = "<div>\
    <p class=\"intro\">Some text</p>\
 </div>\
 ";
```

Include paths
-------------

The rule is that the path to the mentioned files is always relative
to the source file that is processed.
So with nested includes, the path for the child is always relative to the 
directory of the parent file.




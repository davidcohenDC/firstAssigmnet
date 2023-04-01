# Java Walker
Java Walker is a multi-threaded application that walks through a directory and its subdirectories, 
reads all files in the directory that have a given extension, and outputs the number of lines in each file, broken
down into intervals. The application also prints out the filenames of the top N files by number of lines.

## Features
* Walks through a directory and its subdirectories, reads all files in the directory that have a given extension
* Outputs the number of lines in each file, broken down into intervals;
* Prints out the filenames of the top N files by number of lines.
* Multi-threaded to improve performance;
* Can be run from the command line.
* Can be run from a GUI.

## Getting Started
To use Java Walker, simply run the '**WalkerGUI**' class. The GUI interface will allow you to specify the directory you 
want to walk through, the file extension to filter on, and the maximum number of files to display in the output.

## Usage 
1. Run the '**WalkerGUI**' class.
2. Choose the directory you want to walk through.
3. Enter the maximum number of files to display in the output.
4. Enter the number of lines to break the output into intervals.
5. Enter the maximum number of intervals to display in the output.
6. Click the '**Start**' button to start the walk.
7. The application will display the number of lines in each file broken down into intervals, as well as the filenames 
8. of the top N files by number of lines.
9. Click the '**Stop**' button to stop the walk.
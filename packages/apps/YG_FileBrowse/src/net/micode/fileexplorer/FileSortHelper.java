/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.micode.fileexplorer;

import java.util.Comparator;
import java.util.HashMap;

import com.yecon.filemanager.FileInfo;

public class FileSortHelper {

        public static final String SORT_NAME = "name";

        public static final String SORT_SIZE = "size";

        public static final String SORT_DATE = "date";

        public static final String SORT_TYPE = "type";


    private String mSort;

    private boolean mFileFirst;

    private HashMap<String, Comparator> mComparatorList = new HashMap<String, Comparator>();

    public FileSortHelper() {
        mSort = SORT_NAME;
        mComparatorList.put(SORT_NAME, cmpName);
        mComparatorList.put(SORT_SIZE, cmpSize);
        mComparatorList.put(SORT_DATE, cmpDate);
        mComparatorList.put(SORT_TYPE, cmpType);
    }

    public void setSortMethog(String s) {
        mSort = s;
    }

    public String getSortMethod() {
        return mSort;
    }

    public void setFileFirst(boolean f) {
        mFileFirst = f;
    }

    public Comparator getComparator(String sort) {
        return mComparatorList.get(sort);
    }

    private abstract class FileComparator implements Comparator<FileInfo> {

        @Override
        public int compare(FileInfo object1, FileInfo object2) {
            if (object1.isDir == object2.isDir) {
                return doCompare(object1, object2);
            }

            if (mFileFirst) {
                // the files are listed before the dirs
                return (object1.isDir ? 1 : -1);
            } else {
                // the dir-s are listed before the files
                return object1.isDir ? -1 : 1;
            }
        }

        protected abstract int doCompare(FileInfo object1, FileInfo object2);
    }

    private Comparator cmpName = new FileComparator() {
        @Override
        public int doCompare(FileInfo object1, FileInfo object2) {
            return object1.fileName.compareToIgnoreCase(object2.fileName);
        }
    };

    private Comparator cmpSize = new FileComparator() {
        @Override
        public int doCompare(FileInfo object1, FileInfo object2) {
            return longToCompareInt(object1.size - object2.size);
        }
    };

    private Comparator cmpDate = new FileComparator() {
        @Override
        public int doCompare(FileInfo object1, FileInfo object2) {
            return longToCompareInt(object2.modifiedTime - object1.modifiedTime);
        }
    };

    private int longToCompareInt(long result) {
        return result > 0 ? 1 : (result < 0 ? -1 : 0);
    }

    private Comparator cmpType = new FileComparator() {
        @Override
        public int doCompare(FileInfo object1, FileInfo object2) {
            int result = Util.getExtFromFilename(object1.fileName).compareToIgnoreCase(
                    Util.getExtFromFilename(object2.fileName));
            if (result != 0)
                return result;

            return Util.getNameFromFilename(object1.fileName).compareToIgnoreCase(
                    Util.getNameFromFilename(object2.fileName));
        }
    };
}

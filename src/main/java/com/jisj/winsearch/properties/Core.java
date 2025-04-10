package com.jisj.winsearch.properties;

/**
 * Enum of <a href="https://learn.microsoft.com/en-us/windows/win32/properties/core-bumper">Windows Core properties</a>
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props">Windows Property System</a>
 *
 */
public enum Core implements WinProperty{
    /**
     * Core - <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-dateaccessed">System.DateAccessed</a> Indicates the last time the item was accessed
     */
    SystemDateAccessed("System.DateAccessed"),
    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-fileattributes">System.FileAttributes</a> The attributes of the item
     */
    SystemFileAttributes("System.FileAttributes"),
    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-fileextension">System.FileExtension</a> Identifies the file extension of the file-based item, including the leading period
     */
    SystemFileExtension("System.FileExtension"),
    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-isread">System.IsRead</a> Identifies whether the item has been read
     */
    SystemIsRead("System.IsRead"),
    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-filename">System.FileName</a> The file name, including its extension.
     */
    SystemFileName("System.FileName"),
    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-foldernamedisplay">System.FolderNameDisplay</a> This property is similar to {@link #SystemItemNameDisplay System.ItemNameDisplay} except it is only set for folders, for files it will be empty.
     * This is useful to segregate files and folders by using this as the first sort key
     */
    SystemFolderNameDisplay("System.FolderNameDisplay"),
    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-itemauthors">System.ItemAuthors</a> Generic list of authors associated with an item. For example, the artist name for a music track is the item author
     */
    SystemItemAuthors("System.ItemAuthors"),
    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-itemdate">System.ItemDate</a> The primary date of interest for an item
     */
    SystemItemDate("System.ItemDate"),
    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-itemfoldernamedisplay">System.ItemFolderNameDisplay</a> The user-friendly display name of an item's parent folder. This is derived from {@link #SystemItemFolderPathDisplay System.ItemFolderPathDisplay}
     */
    SystemItemFolderNameDisplay("System.ItemFolderNameDisplay"),
    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-itemfolderpathdisplay">System.ItemFolderPathDisplay</a> The user-friendly display path of an item's parent folder
     */
    SystemItemFolderPathDisplay("System.ItemFolderPathDisplay"),
    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-itemname">System.ItemName</a> The base name of the System.ItemNameDisplay property
     */
    SystemItemName("System.ItemName"),
    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-itemnamedisplay">System.ItemNameDisplay</a> The display name in "most complete" form. It is the unique representation of the item name most appropriate for end users
     */
    SystemItemNameDisplay("System.ItemNameDisplay"),
    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-itemnamedisplaywithoutextension">System.ItemNameDisplayWithoutExtension</a> This is similar to {@link #SystemItemNameDisplay System.ItemNameDisplay} except that it never includes a file extension
     */
    SystemItemNameDisplayWithoutExtension("System.ItemNameDisplayWithoutExtension"),
    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-itempathdisplay">System.ItemPathDisplay</a> The user-friendly display path to the item
     */
    SystemItemPathDisplay("System.ItemPathDisplay"),
    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-itemurl">System.ItemUrl</a> Represents a well-formed URL that points to the item
     */
    SystemItemUrl("System.ItemUrl"),
    SystemIsAttachment("System.IsAttachment"),
    SystemIsDeleted("System.IsDeleted"),
    SystemItemTypeText("System.ItemTypeText"),
    SystemKind("System.Kind"),
    SystemKindText("System.KindText"),
    SystemFlagStatusText("System.FlagStatusText"),
    SystemSearchContainerHash("System.Search.ContainerHash"),
    SystemSearchStore("System.Search.Store"),
    SystemKeywords("System.Keywords");

    private final String name;

    Core(String name) {
        this.name = name;
    }

    /**
     * Returns string name of property
     *
     * @return {@link String}
     */
    @Override
    public String getName() {
        return name;
    }

}

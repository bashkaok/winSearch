package org.mikesoft.winsearch.properties;

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
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-filename">System.FileName</a> The file name, including its extension.
     */
    SystemFileName("System.FileName"),
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
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-itempathdisplay">System.ItemPathDisplay</a> The user-friendly display path to the item
     */
    SystemItemPathDisplay("System.ItemPathDisplay"),
    /**
     * <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props-system-itemurl">System.ItemUrl</a> Represents a well-formed URL that points to the item
     */
    SystemItemUrl("System.ItemUrl"),
    SystemDueDate("System.DueDate"),
    SystemIsAttachment("System.IsAttachment"),
    SystemIsDeleted("System.IsDeleted"),
    SystemItemNamePrefix("System.ItemNamePrefix"),
    SystemItemTypeText("System.ItemTypeText"),
    SystemItemParticipants("System.ItemParticipants"),
    SystemKind("System.Kind"),
    SystemKindText("System.KindText"),
    SystemIsIncomplete("System.IsIncomplete"),
    SystemIsFlaggedComplete("System.IsFlaggedComplete"),
    SystemIsFlagged("System.IsFlagged"),
    SystemFlagStatusText("System.FlagStatusText"),
    SystemIdentity("System.Identity"),
    SystemIsRead("System.IsRead"),
    SystemImportance("System.Importance"),
    SystemSearchContainerHash("System.Search.ContainerHash"),
    SystemSearchStore("System.Search.Store"),
    SystemEndDate("System.EndDate"),
    SystemStartDate("System.StartDate"),
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

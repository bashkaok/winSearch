package org.mikesoft.winsearch;

/**
 * Enum of <a href="https://learn.microsoft.com/en-us/windows/win32/properties/props">Windows Property System</a>
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/search/-search-3x-wds-propertymappings">MSLearn Property mapping</a>
 */
public enum Property {
    SystemItemFolderPathDisplay("System.ItemFolderPathDisplay"),
    SystemItemPathDisplay("System.ItemPathDisplay"),
    SystemItemAuthors("System.ItemAuthors"),
    SystemItemNamePrefix("System.ItemNamePrefix"),
    SystemItemName("System.ItemName"),
    SystemItemTypeText("System.ItemTypeText"),
    SystemItemFolderNameDisplay("System.ItemFolderNameDisplay"),
    SystemIsAttachment("System.IsAttachment"),
    SystemIsDeleted("System.IsDeleted"),
    SystemDateAccessed("System.DateAccessed"),
    SystemItemParticipants("System.ItemParticipants"),
    SystemKind("System.Kind"),
    SystemKindText("System.KindText"),
    SystemItemDate("System.ItemDate"),
    SystemDueDate("System.DueDate"),
    SystemIsIncomplete("System.IsIncomplete"),
    SystemIsFlaggedComplete("System.IsFlaggedComplete"),
    SystemIsFlagged("System.IsFlagged"),
    SystemFlagStatusText("System.FlagStatusText"),
    SystemIdentity("System.Identity"),
    SystemIsRead("System.IsRead"),
    SystemImportance("System.Importance"),
    SystemSearchContainerHash("System.Search.ContainerHash"),
    SystemSearchStore("System.Search.Store"),
    SystemFileExtension("System.FileExtension"),
    SystemFileName("System.FileName"),
    SystemEndDate("System.EndDate"),
    SystemStartDate("System.StartDate"),
    SystemKeywords("System.Keywords");

    public final String name;

    Property(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

CREATE TABLE [dbo].[Users] (
    [Id]          INT             IDENTITY (1, 1) NOT NULL,
    [loginName]   VARCHAR (50)    NOT NULL,
    [password]    VARCHAR (70)    NOT NULL,
    [phoneNumber] VARCHAR (13)    NOT NULL,
    [photo]       VARBINARY (MAX) NULL,
    [gender]      VARCHAR (50)    NOT NULL,
    CONSTRAINT [PK_Users] PRIMARY KEY CLUSTERED ([Id] ASC)
);

CREATE TABLE [dbo].[Groups] (
    [Id]        INT          IDENTITY (1, 1) NOT NULL,
    [GroupName] VARCHAR (50) NOT NULL,
    CONSTRAINT [PK_NewTable] PRIMARY KEY CLUSTERED ([Id] ASC, [GroupName] ASC)
);
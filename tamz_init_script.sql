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
    [GroupCreator] VARCHAR (50) NOT NULL,
    CONSTRAINT [PK_Groups] PRIMARY KEY CLUSTERED ([Id] ASC)
);

CREATE TABLE [dbo].[GroupsUsers] (
    [Id]     INT IDENTITY (1, 1) NOT NULL,
    [GroupId] INT NOT NULL,
    [UserId]  INT NOT NULL,
    CONSTRAINT [PK_GroupsUsers] PRIMARY KEY CLUSTERED ([Id] ASC),
    CONSTRAINT [FK_GroupsUsers_Groups] FOREIGN KEY ([GroupId]) REFERENCES [dbo].[Groups] ([Id]),
    CONSTRAINT [FK_GroupsUsers_Users] FOREIGN KEY ([UserId]) REFERENCES [dbo].[Users] ([Id])
);

CREATE TABLE [dbo].[Events] (
    [Id]           INT             IDENTITY (1, 1) NOT NULL,
    [GroupId]      INT             NOT NULL,
    [CreatorId]    INT             NOT NULL,
    [EventName]    VARCHAR (100)   NOT NULL, 
    [EventDate]    DATETIME        NOT NULL,
    [Location]     VARCHAR (200)   NOT NULL,
    [AccommodationProvided] BIT    NOT NULL DEFAULT 0,
    [Description]  VARCHAR (MAX)   NULL, 
    CONSTRAINT [PK_Events] PRIMARY KEY CLUSTERED ([Id] ASC),
    CONSTRAINT [FK_Events_Groups] FOREIGN KEY ([GroupId]) REFERENCES [dbo].[Groups] ([Id]),
    CONSTRAINT [FK_Events_Users] FOREIGN KEY ([CreatorId]) REFERENCES [dbo].[Users] ([Id])
);


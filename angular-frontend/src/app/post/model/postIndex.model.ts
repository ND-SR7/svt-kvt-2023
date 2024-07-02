export class PostIndex {
    id: string;
    title: string;
    fullContent: string;
    fileContent: string;
    numberOfLikes: number;
    numberOfComments: number;
    commentContent: string;
    databaseId: number;
    
    constructor(obj: {
        id?: string,
        title?: string,
        fullContent?: string,
        fileContent?: string,
        numberOfLikes?: number,
        numberOfComments?: number,
        commentContent?: string,
        databaseId?: number
    } = {}) {
        this.id = obj.id || null as unknown as string;
        this.title = obj.title || null as unknown as string;
        this.fullContent = obj.fullContent || null as unknown as string;
        this.fileContent = obj.fileContent || null as unknown as string;
        this.numberOfLikes = obj.numberOfLikes || null as unknown as number;
        this.numberOfComments = obj.numberOfComments || null as unknown as number;
        this.commentContent = obj.commentContent || null as unknown as string;
        this.databaseId = obj.databaseId || null as unknown as number;
    }
}

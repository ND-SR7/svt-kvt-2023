export class GroupIndex {
    id: string;
    name: string;
    description: string;
    fileContent: string;
    numberOfPosts: number;
    rules: string;
    averageLikes: number;
    databaseId: number;
    
    constructor(obj: {
        id?: string,
        name?: string,
        description?: string,
        fileContent?: string,
        numberOfPosts?: number,
        rules?: string,
        averageLikes?: number,
        databaseId?: number
    } = {}) {
        this.id = obj.id || null as unknown as string;
        this.name = obj.name || null as unknown as string;
        this.description = obj.description || null as unknown as string;
        this.fileContent = obj.fileContent || null as unknown as string;
        this.numberOfPosts = obj.numberOfPosts || null as unknown as number;
        this.rules = obj.rules || null as unknown as string;
        this.averageLikes = obj.averageLikes || null as unknown as number;
        this.databaseId = obj.databaseId || null as unknown as number;
    }
}

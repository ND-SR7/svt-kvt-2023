export class Group {
    _id: number;
    name: string;
    description: string;
    creationDate: Date;
    isSuspended: boolean;
    suspendedReason: string;

    constructor(obj: {
        _id?: number,
        name?: string,
        description?: string,
        creationDate?: Date,
        isSuspended?: boolean,
        suspendedReason?: string
    } = {}) {
        this._id = obj._id || null as unknown as number;
        this.name = obj.name || null as unknown as string;
        this.description = obj.description || null as unknown as string;
        this.creationDate = obj.creationDate || null as unknown as Date;
        this.isSuspended = obj.isSuspended || null as unknown as boolean;
        this.suspendedReason = obj.suspendedReason || null as unknown as string;
    }

    get id(): number {
        return this._id;
    }
}

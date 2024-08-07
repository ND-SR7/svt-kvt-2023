export class Group {
    _id: number;
    name: string;
    description: string;
    creationDate: string;
    suspended: boolean;
    suspendedReason: string;
    rules: string;
    filename: string;

    constructor(obj: {
        _id?: number,
        name?: string,
        description?: string,
        creationDate?: string,
        suspended?: boolean,
        suspendedReason?: string,
        rules?: string,
        filename?: string
    } = {}) {
        this._id = obj._id || null as unknown as number;
        this.name = obj.name || null as unknown as string;
        this.description = obj.description || null as unknown as string;
        this.creationDate = obj.creationDate || null as unknown as string;
        this.suspended = obj.suspended || null as unknown as boolean;
        this.suspendedReason = obj.suspendedReason || null as unknown as string;
        this.rules = obj.rules || null as unknown as string;
        this.filename = obj.filename || null as unknown as string;
    }

    get id(): number {
        return this._id;
    }
}

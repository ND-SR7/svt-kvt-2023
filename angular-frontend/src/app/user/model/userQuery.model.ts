export class UserQuery {
    name1: string;
    name2: string;

    constructor(obj : {
        name1?: string,
        name2?: string
    } = {}) {
        this.name1 = obj.name1 || null as unknown as string;
        this.name2 = obj.name2 || null as unknown as string;
    }
}

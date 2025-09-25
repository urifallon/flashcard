export interface ExampleData {
    id: number;
    name: string;
    description: string;
}

export interface ApiResponse<T> {
    data: T;
    message: string;
    status: number;
}

export type ExampleList = ExampleData[];